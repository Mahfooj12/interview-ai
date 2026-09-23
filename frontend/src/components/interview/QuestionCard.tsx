"use client";

import { Lightbulb } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import type { QuestionResponse } from "@/types/interview";

export function QuestionCard({ question }: { question: QuestionResponse }) {
  return (
    <Card>
      <CardHeader className="space-y-2">
        <div className="flex flex-wrap items-center gap-2">
          <Badge variant="secondary">{question.type.replace("_", " ")}</Badge>
          <Badge variant="outline">{question.difficulty}</Badge>
          <span className="text-xs text-muted-foreground">
            #{question.order}
          </span>
        </div>
        <CardTitle className="text-lg leading-relaxed">
          {question.text}
        </CardTitle>
      </CardHeader>
      {question.expectedTopics.length > 0 ? (
        <CardContent className="pt-0">
          <div className="flex items-start gap-2 rounded-md bg-muted p-3 text-sm text-muted-foreground">
            <Lightbulb className="mt-0.5 h-4 w-4 shrink-0" />
            <div>
              <p className="font-medium text-foreground">Topics to cover</p>
              <ul className="list-disc pl-5">
                {question.expectedTopics.map((topic) => (
                  <li key={topic}>{topic}</li>
                ))}
              </ul>
            </div>
          </div>
        </CardContent>
      ) : null}
    </Card>
  );
}