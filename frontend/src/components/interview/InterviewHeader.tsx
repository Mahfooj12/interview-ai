"use client";

import { Clock } from "lucide-react";
import { Badge } from "@/components/ui/badge";

interface InterviewHeaderProps {
  type: string;
  difficulty: string;
  mode: string;
  status: string;
  questionNumber: number;
  totalHint: number;
}

export function InterviewHeader({
  type,
  difficulty,
  mode,
  status,
  questionNumber,
  totalHint,
}: InterviewHeaderProps) {
  return (
    <div className="flex flex-wrap items-center justify-between gap-3 border-b pb-4">
      <div className="space-y-1">
        <h1 className="text-xl font-semibold">
          {type.replace("_", " ")} interview
        </h1>
        <div className="flex flex-wrap gap-2 text-xs">
          <Badge variant="outline">{difficulty}</Badge>
          <Badge variant="outline">{mode}</Badge>
          <Badge variant="secondary">{status}</Badge>
        </div>
      </div>
      <div className="flex items-center gap-2 text-sm text-muted-foreground">
        <Clock className="h-4 w-4" />
        <span>
          Question {questionNumber} of ~{totalHint}
        </span>
      </div>
    </div>
  );
}