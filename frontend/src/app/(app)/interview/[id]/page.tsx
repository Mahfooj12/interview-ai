"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { motion, AnimatePresence } from "framer-motion";

import {
  useCompleteInterview,
  useInterview,
  useNextQuestion,
  useStartInterview,
  useSubmitAnswer,
} from "@/hooks/useInterview";

import { InterviewHeader } from "@/components/interview/InterviewHeader";
import { QuestionCard } from "@/components/interview/QuestionCard";
import { AnswerInput } from "@/components/interview/AnswerInput";
import { EvaluationPanel } from "@/components/interview/EvaluationPanel";
import { WeakAreasPanel } from "@/components/interview/WeakAreasPanel";
import { EndInterviewDialog } from "@/components/interview/EndInterviewDialog";

import { Skeleton } from "@/components/ui/skeleton";
import { Button } from "@/components/ui/button";
import { Spinner } from "@/components/ui/spinner";

import type {
  EvaluationResponse,
  NextQuestionResponse,
  QuestionResponse,
} from "@/types/interview";

const DEFAULT_TOTAL_HINT = 10;

export default function LiveInterviewPage() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const interviewId = params.id;

  const { data: interview, isLoading } = useInterview(interviewId);
  const start = useStartInterview();
  const submit = useSubmitAnswer(interviewId);
  const next = useNextQuestion(interviewId);
  const complete = useCompleteInterview(interviewId);

  const [question, setQuestion] = useState<QuestionResponse | null>(null);
  const [lastEvaluation, setLastEvaluation] =
    useState<EvaluationResponse | null>(null);
  const [questionNumber, setQuestionNumber] = useState(1);
  const [weakAreas, setWeakAreas] = useState<string[]>([]);
  const [strongAreas, setStrongAreas] = useState<string[]>([]);
  const [cumulativeScore, setCumulativeScore] = useState(0);
  const [completed, setCompleted] = useState(false);
  const [starting, setStarting] = useState(false);

  // Bootstrap: on mount, start or resume the interview.
  useEffect(() => {
    if (!interviewId || isLoading || !interview) return;
    if (completed) return;

    if (
      interview.status === "SCHEDULED" ||
      interview.status === "IN_PROGRESS"
    ) {
      setStarting(true);
      start
        .mutateAsync(interviewId)
        .then((res: NextQuestionResponse) => {
          setQuestion(res.question);
          setQuestionNumber(res.questionNumber);
          setWeakAreas(res.weakAreas);
          setStrongAreas(res.strongAreas);
          setCumulativeScore(res.cumulativeScore);
          setCompleted(res.completed);
        })
        .finally(() => setStarting(false));
    }

    if (interview.status === "COMPLETED") {
      setCompleted(true);
      router.replace(`/interview/${interviewId}/result`);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [interviewId, isLoading, interview?.status]);

  const handleSubmit = async (text: string) => {
    if (!question) return;
    const res = await submit.mutateAsync({
      questionId: question.id,
      text,
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

    if (res.nextQuestion.question) {
      setQuestion(res.nextQuestion.question);
      setQuestionNumber(res.nextQuestion.questionNumber);
    } else {
      // Defensive: fetch next question explicitly.
      const fallback = await next.mutateAsync();
      if (fallback.completed || !fallback.question) {
        setCompleted(true);
        router.replace(`/interview/${interviewId}/result`);
      } else {
        setQuestion(fallback.question);
        setQuestionNumber(fallback.questionNumber);
      }
    }
  };

  const handleEnd = async () => {
    await complete.mutateAsync();
    setCompleted(true);
    router.replace(`/interview/${interviewId}/result`);
  };

  if (isLoading || starting || !interview) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-16 w-full" />
        <Skeleton className="h-40 w-full" />
        <Skeleton className="h-40 w-full" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <InterviewHeader
        type={interview.type}
        difficulty={interview.difficulty}
        mode={interview.mode}
        status={completed ? "COMPLETED" : interview.status}
        questionNumber={questionNumber}
        totalHint={DEFAULT_TOTAL_HINT}
      />

      <div className="grid gap-6 lg:grid-cols-3">
        <div className="space-y-4 lg:col-span-2">
          {question ? (
            <AnimatePresence mode="wait">
              <motion.div
                key={question.id}
                initial={{ opacity: 0, y: 8 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -8 }}
                transition={{ duration: 0.2 }}
              >
                <QuestionCard question={question} />
              </motion.div>
            </AnimatePresence>
          ) : (
            <Skeleton className="h-40 w-full" />
          )}

          <AnswerInput
            onSubmit={handleSubmit}
            submitting={submit.isPending}
            disabled={completed || !question}
          />
        </div>

        <div className="space-y-4">
          <WeakAreasPanel
            weakAreas={weakAreas}
            strongAreas={strongAreas}
            cumulativeScore={cumulativeScore}
          />

          {lastEvaluation ? (
            <EvaluationPanel evaluation={lastEvaluation} />
          ) : null}

          <div className="flex justify-end">
            {completed ? (
              <Button onClick={() => router.push(`/interview/${interviewId}/result`)}>
                View report
              </Button>
            ) : (
              <EndInterviewDialog
                onConfirm={handleEnd}
                ending={complete.isPending}
              />
            )}
          </div>

          {submit.isPending ? (
            <div className="flex items-center gap-2 text-sm text-muted-foreground">
              <Spinner /> Evaluating your answer…
            </div>
          ) : null}
        </div>
      </div>
    </div>
  );
}