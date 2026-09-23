"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { motion, AnimatePresence } from "framer-motion";
import { Mic, Square, Pause, Play } from "lucide-react";

import { InterviewHeader } from "@/components/interview/InterviewHeader";
import { QuestionCard } from "@/components/interview/QuestionCard";
import { EvaluationPanel } from "@/components/interview/EvaluationPanel";
import { WeakAreasPanel } from "@/components/interview/WeakAreasPanel";
import { EndInterviewDialog } from "@/components/interview/EndInterviewDialog";
import { LiveTranscript } from "./LiveTranscript";

import {
  useInterview,
  useStartInterview,
  useCompleteInterview,
} from "@/hooks/useInterview";
import { useVoiceInterview } from "@/hooks/useVoiceInterview";
import { useInterviewerState } from "@/hooks/useInterviewerState";
import { useAutoListen } from "@/hooks/useAutoListen";

import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { Spinner } from "@/components/ui/spinner";
import { toast } from "sonner";

const DEFAULT_TOTAL_HINT = 10;
const SPEAKING_GAP_MS = 400;
const SILENCE_LIMIT_MS = 8000;

export function VoiceInterviewPanel() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const interviewId = params.id;

  const { data: interview, isLoading } = useInterview(interviewId);
  const start = useStartInterview();
  const complete = useCompleteInterview(interviewId);

  const voice = useVoiceInterview(interviewId);
  const iv = useInterviewerState("IDLE");

  const [autoMode, setAutoMode] = useState(true);
  const [hasStarted, setHasStarted] = useState(false);
  const [elapsedSec, setElapsedSec] = useState(0);

  const submittingRef = useRef(false);
  const lastSubmittedQuestionId = useRef<string | null>(null);

  const autoListen = useAutoListen({
    silenceMs: SILENCE_LIMIT_MS,
    minSpeechMs: 1500,
    maxListenMs: 180000,
    onTick: (ms) => setElapsedSec(Math.floor(ms / 1000)),
    onDone: (reason) => {
      console.log(`[Voice] auto-listen done (${reason})`);
      void handleSubmitAnswer(reason);
    },
  });

  // ⚡ Wire recognizer speech events into auto-listen.
  useEffect(() => {
    voice.registerSpeechListener(() => {
      autoListen.pingSpeech();
    });
    return () => voice.registerSpeechListener(null);
  }, [voice, autoListen]);

  // Boot
  useEffect(() => {
    if (!interviewId || isLoading || !interview) return;
    if (interview.status === "COMPLETED") {
      router.replace(`/interview/${interviewId}/result`);
      return;
    }
    if (hasStarted) return;
    if (interview.status === "SCHEDULED" || interview.status === "IN_PROGRESS") {
      setHasStarted(true);
      start
        .mutateAsync(interviewId)
        .then((res) => voice.setCurrentQuestion(res.question))
        .catch(() => {});
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [interviewId, isLoading, interview?.status, hasStarted]);

  // Auto-start listening when TTS finishes
  useEffect(() => {
    if (voice.speaking) {
      iv.setState("SPEAKING");
      return;
    }
    if (iv.isSpeaking) {
      iv.setState("WAITING");
      if (autoMode && !autoListen.isActive && !voice.submitting) {
        const t = setTimeout(() => beginListening(), SPEAKING_GAP_MS);
        return () => clearTimeout(t);
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [voice.speaking, autoMode, voice.submitting]);

  const beginListening = useCallback(() => {
    if (!voice.speechSupported) {
      toast.error(
        "Your browser does not support live transcription. Please use Chrome or Edge."
      );
      return;
    }
    if (autoListen.isActive) return;
    console.log("[Voice] begin listening");
    lastSubmittedQuestionId.current = null;
    voice.startListening();
    autoListen.start();
    iv.setState("LISTENING");
  }, [autoListen, iv, voice]);

  const handleSubmitAnswer = useCallback(
    async (reason: "silence" | "maxTime" | "manual") => {
      if (submittingRef.current) return;
      if (!voice.question) return;
      if (lastSubmittedQuestionId.current === voice.question.id) return;

      submittingRef.current = true;
      lastSubmittedQuestionId.current = voice.question.id;
      autoListen.stop();

      await voice.stopListening();
      await new Promise((r) => setTimeout(r, 250));

      const transcript = (
        voice.liveTranscript ||
        voice.transcript ||
        ""
      ).trim();

      console.log(`[Voice] submitting (${reason}):`, transcript);

      if (!transcript) {
        toast.info("Nothing heard — try speaking a bit longer.");
        iv.setState("WAITING");
        submittingRef.current = false;
        if (autoMode) setTimeout(() => beginListening(), 400);
        return;
      }

      iv.setState("THINKING");
      try {
        await voice.sendAnswer(transcript);
      } catch (e) {
        console.error("[Voice] submit failed", e);
        iv.setState("WAITING");
      } finally {
        submittingRef.current = false;
      }
    },
    [autoListen, autoMode, beginListening, iv, voice]
  );

  const handleManualToggle = () => {
    if (autoListen.isActive) {
      void handleSubmitAnswer("manual");
    } else {
      beginListening();
    }
  };

  const handleToggleAutoMode = () => setAutoMode((v) => !v);

  const handleEnd = async () => {
    iv.setState("ENDING");
    voice.stopSpeaking();
    autoListen.stop();
    await voice.stopListening();
    await complete.mutateAsync();
    router.replace(`/interview/${interviewId}/result`);
  };

  if (isLoading || !interview) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-16 w-full" />
        <Skeleton className="h-40 w-full" />
        <Skeleton className="h-40 w-full" />
      </div>
    );
  }

  const silenceRemaining = autoListen.hasSpoken
    ? Math.max(0, Math.ceil((SILENCE_LIMIT_MS - autoListen.silenceMs) / 1000))
    : null;

  return (
    <div className="space-y-6">
      <InterviewHeader
        type={interview.type}
        difficulty={interview.difficulty}
        mode="VOICE"
        status={voice.completed ? "COMPLETED" : interview.status}
        questionNumber={voice.questionNumber}
        totalHint={DEFAULT_TOTAL_HINT}
      />

      <div className="flex flex-wrap items-center justify-between gap-3 rounded-lg border bg-card px-4 py-3">
        <div className="flex flex-wrap items-center gap-3">
          <span className={`h-2.5 w-2.5 rounded-full ${iv.color} animate-pulse`} />
          <span className="text-sm font-medium">{iv.label}</span>
          {autoListen.isActive && elapsedSec > 0 ? (
            <span className="text-xs text-muted-foreground">
              ({elapsedSec}s)
            </span>
          ) : null}
          {autoListen.isActive && silenceRemaining !== null && silenceRemaining < 8 ? (
            <span className="text-xs font-medium text-amber-600">
              Auto-submits in {silenceRemaining}s
            </span>
          ) : null}
        </div>
        <Button
          size="sm"
          variant={autoMode ? "default" : "outline"}
          onClick={handleToggleAutoMode}
        >
          {autoMode ? (
            <>
              <Pause className="mr-1 h-3 w-3" />
              Auto mode ON
            </>
          ) : (
            <>
              <Play className="mr-1 h-3 w-3" />
              Manual mode
            </>
          )}
        </Button>
      </div>

      <div className="grid gap-6 lg:grid-cols-3">
        <div className="space-y-4 lg:col-span-2">
          {voice.question ? (
            <AnimatePresence mode="wait">
              <motion.div
                key={voice.question.id}
                initial={{ opacity: 0, y: 8 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -8 }}
                transition={{ duration: 0.2 }}
              >
                <QuestionCard question={voice.question} />
              </motion.div>
            </AnimatePresence>
          ) : (
            <Skeleton className="h-40 w-full" />
          )}

          <div className="flex flex-col items-center gap-2 rounded-lg border bg-card p-4">
            <Button
              onClick={handleManualToggle}
              disabled={
                voice.submitting ||
                voice.speaking ||
                iv.isThinking ||
                !voice.question
              }
              variant={autoListen.isActive ? "destructive" : "default"}
              className="rounded-full"
            >
              {autoListen.isActive ? (
                <>
                  <Square className="mr-2 h-4 w-4" />
                  Stop &amp; send
                </>
              ) : (
                <>
                  <Mic className="mr-2 h-4 w-4" />
                  Start speaking
                </>
              )}
            </Button>
            <p className="text-xs text-muted-foreground">
              {autoMode
                ? `Auto mode — the interviewer listens and auto-submits after ${SILENCE_LIMIT_MS / 1000}s of silence.`
                : "Manual mode — click to start and stop each answer."}
            </p>
          </div>

          <LiveTranscript
            segments={voice.segments}
            finalText={voice.liveTranscript || voice.transcript}
          />

          {voice.interimTranscript ? (
            <p className="text-xs italic text-muted-foreground">
              Listening: {voice.interimTranscript}
            </p>
          ) : null}
        </div>

        <div className="space-y-4">
          <WeakAreasPanel
            weakAreas={voice.weakAreas}
            strongAreas={voice.strongAreas}
            cumulativeScore={voice.cumulativeScore}
          />

          {voice.lastEvaluation ? (
            <EvaluationPanel evaluation={voice.lastEvaluation} />
          ) : null}

          <div className="flex justify-end">
            <EndInterviewDialog
              onConfirm={handleEnd}
              ending={complete.isPending}
            />
          </div>

          {voice.submitting ? (
            <div className="flex items-center gap-2 text-sm text-muted-foreground">
              <Spinner /> Evaluating your answer…
            </div>
          ) : null}
        </div>
      </div>
    </div>
  );
}