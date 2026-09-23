"use client";

import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import type { EvaluationResponse } from "@/types/interview";

interface EvaluationPanelProps {
  evaluation: EvaluationResponse;
}

const DIMENSIONS: { key: keyof EvaluationResponse; label: string }[] = [
  { key: "technicalAccuracy", label: "Technical accuracy" },
  { key: "relevance", label: "Relevance" },
  { key: "depth", label: "Depth" },
  { key: "clarity", label: "Clarity" },
  { key: "communication", label: "Communication" },
  { key: "confidence", label: "Confidence" },
];

export function EvaluationPanel({ evaluation }: EvaluationPanelProps) {
  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between space-y-0">
        <CardTitle className="text-base">Feedback</CardTitle>
        <Badge variant="success">{Math.round(evaluation.overall)}/100</Badge>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="grid gap-3 sm:grid-cols-2">
          {DIMENSIONS.map(({ key, label }) => {
            const value = Number(evaluation[key] ?? 0);
            return (
              <div key={String(key)} className="space-y-1">
                <div className="flex items-center justify-between text-xs">
                  <span className="text-muted-foreground">{label}</span>
                  <span className="font-medium">{Math.round(value)}</span>
                </div>
                <Progress value={value} />
              </div>
            );
          })}
        </div>

        {evaluation.feedback ? (
          <div>
            <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
              Interviewer feedback
            </p>
            <p className="mt-1 text-sm">{evaluation.feedback}</p>
          </div>
        ) : null}

        {evaluation.strengths.length > 0 ? (
          <div className="flex flex-wrap gap-1">
            {evaluation.strengths.map((s) => (
              <Badge key={s} variant="success">
                {s}
              </Badge>
            ))}
          </div>
        ) : null}

        {evaluation.weaknesses.length > 0 ? (
          <div className="flex flex-wrap gap-1">
            {evaluation.weaknesses.map((s) => (
              <Badge key={s} variant="warning">
                {s}
              </Badge>
            ))}
          </div>
        ) : null}

        {evaluation.idealAnswer ? (
          <div className="rounded-md bg-muted p-3 text-sm">
            <p className="font-medium">Ideal answer</p>
            <p className="text-muted-foreground">{evaluation.idealAnswer}</p>
          </div>
        ) : null}
      </CardContent>
    </Card>
  );
}