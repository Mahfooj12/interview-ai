"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import type { QuestionFeedback } from "@/types/report";

interface QuestionFeedbackListProps {
  feedback: QuestionFeedback[];
}

export function QuestionFeedbackList({ feedback }: QuestionFeedbackListProps) {
  if (!feedback || feedback.length === 0) {
    return null;
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Question-by-question feedback</CardTitle>
      </CardHeader>
      <CardContent className="space-y-5">
        {feedback.map((item, idx) => (
          <div key={`${item.questionId}-${idx}`} className="space-y-2">
            <div className="flex items-start justify-between gap-3">
              <p className="font-medium leading-relaxed">
                {idx + 1}. {item.question ?? "—"}
              </p>
              <Badge variant={scoreVariant(item.score)}>
                {Math.round(item.score)}/100
              </Badge>
            </div>

            {item.answer ? (
              <div className="rounded-md bg-muted p-3 text-sm">
                <p className="font-medium">Your answer</p>
                <p className="whitespace-pre-wrap text-muted-foreground">
                  {item.answer}
                </p>
              </div>
            ) : null}

            {item.feedback ? (
              <div className="text-sm">
                <p className="font-medium">Feedback</p>
                <p className="text-muted-foreground">{item.feedback}</p>
              </div>
            ) : null}

            {item.idealAnswer ? (
              <div className="rounded-md border border-dashed p-3 text-sm">
                <p className="font-medium">Ideal answer</p>
                <p className="text-muted-foreground">{item.idealAnswer}</p>
              </div>
            ) : null}

            {idx < feedback.length - 1 ? <Separator className="mt-4" /> : null}
          </div>
        ))}
      </CardContent>
    </Card>
  );
}

function scoreVariant(score: number): "success" | "warning" | "destructive" {
  if (score >= 75) return "success";
  if (score >= 55) return "warning";
  return "destructive";
}