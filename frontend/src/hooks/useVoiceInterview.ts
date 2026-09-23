"use client";

import { useCallback, useMemo, useRef, useState } from "react";
import { useRouter } from "next/navigation";
import { toast } from "sonner";

import { useSubmitAnswer } from "@/hooks/useInterview";
import { voiceApi } from "@/lib/api/voice";
import { createSegment, mergeTranscript } from "@/lib/audio/transcript";
import { useSpeechSynthesis } from "@/hooks/useSpeechSynthesis";
import { useSpeechRecognition } from "@/hooks/useSpeechRecognition";

import type {
  EvaluationResponse,
  NextQuestionResponse,
  QuestionResponse,
} from "@/types/interview";
import type { TranscriptSegment } from "@/types/voice";

export function useVoiceInterview(interviewId: string) {
  const router = useRouter();
  const submit = useSubmitAnswer(interviewId);
  const tts = useSpeechSynthesis();

  const [question, setQuestion] = useState<QuestionResponse | null>(null);
  const [segments, setSegments] = useState<TranscriptSegment[]>([]);
  const [lastEvaluation, setLastEvaluation] =
    useState<EvaluationResponse | null>(null);
  const [questionNumber, setQuestionNumber] = useState(1);
  const [weakAreas, setWeakAreas] = useState<string[]>([]);
  const [strongAreas, setStrongAreas] = useState<string[]>([]);
  const [cumulativeScore, setCumulativeScore] = useState(0);
  const [completed, setCompleted] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  // Bridge — lets the panel subscribe to speech events.
  const speechCallbackRef = useRef<(() => void) | null>(null);

  const recognition = useSpeechRecognition({
    lang: "en-US",
    onSpeech: () => {
      speechCallbackRef.current?.();
    },
  });

  const registerSpeechListener = useCallback(
    (cb: (() => void) | null) => {
      speechCallbackRef.current = cb;
    },
    []
  );

  const transcript = useMemo(() => mergeTranscript(segments), [segments]);

  const speakQuestion = useCallback(
    async (text: string) => {
      try {
        const audio = await voiceApi.synthesize(text);
        await tts.speakResponse(audio, text);
      } catch {
        await tts.speakBrowser(text);
      }
    },
    [tts]
  );

  const setCurrentQuestion = useCallback(
    (q: QuestionResponse | null) => {
      setQuestion(q);
      if (q) {
        void speakQuestion(q.text);
      }
    },
    [speakQuestion]
  );

  const appendTranscript = useCallback((text: string) => {
    if (!text.trim()) return;
    setSegments((prev) => [...prev, createSegment(text, true)]);
  }, []);

  const clearTranscript = useCallback(() => {
    setSegments([]);
    recognition.resetTranscript();
  }, [recognition]);

  const sendAnswer = useCallback(
    async (explicitTranscript?: string, audioBlob?: Blob) => {
      if (!question) return;

      let finalTranscript = (explicitTranscript || "").trim();

      if (!finalTranscript) {
        const live = recognition.getTranscript?.() ?? recognition.transcript;
        finalTranscript = (live || "").trim();
      }

      if (!finalTranscript && audioBlob) {
        try {
          const stt = await voiceApi.transcribe(audioBlob);
          if (stt.transcript && stt.transcript.trim()) {
            finalTranscript = stt.transcript;
          }
        } catch (err) {
          console.warn("Backend STT failed:", err);
        }
      }

      if (!finalTranscript) {
        toast.error(
          "No speech detected. Please speak clearly and try again."
        );
        return;
      }

      console.log("[Voice] Submitting answer:", finalTranscript);

      appendTranscript(finalTranscript);
      setSubmitting(true);
      try {
        const res = await submit.mutateAsync({
          questionId: question.id,
          text: finalTranscript,
        });

        setLastEvaluation(res.evaluation);
        setWeakAreas(res.weakAreas);
        setStrongAreas(res.strongAreas);
        setCumulativeScore(res.cumulativeScore);

        if (res.completed || !res.nextQuestion) {
          setCompleted(true);
          router.replace(`/interview/${interviewId}/result`);
          return;
        }

        const next: NextQuestionResponse = res.nextQuestion;
        if (next.question) {
          setQuestionNumber(next.questionNumber);
          clearTranscript();
          setCurrentQuestion(next.question);
        } else {
          setCompleted(true);
          router.replace(`/interview/${interviewId}/result`);
        }
      } finally {
        setSubmitting(false);
      }
    },
    [
      appendTranscript,
      clearTranscript,
      interviewId,
      question,
      recognition,
      router,
      setCurrentQuestion,
      submit,
    ]
  );

  return {
    question,
    questionNumber,
    transcript,
    segments,
    lastEvaluation,
    weakAreas,
    strongAreas,
    cumulativeScore,
    completed,
    submitting,
    speaking: tts.speaking,

    setCurrentQuestion,
    appendTranscript,
    clearTranscript,
    sendAnswer,
    stopSpeaking: tts.stop,

    liveTranscript: recognition.transcript,
    interimTranscript: recognition.interimTranscript,
    isListening: recognition.isListening,
    startListening: recognition.startListening,
    stopListening: recognition.stopListening,
    speechSupported: recognition.supported,
    speechError: recognition.error,

    // NEW
    registerSpeechListener,
  };
}