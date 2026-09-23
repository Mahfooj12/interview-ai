"use client";

import Link from "next/link";
import { ArrowRight } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import type { InterviewStatus, InterviewSummary } from "@/types/interview";

const statusVariant: Record<
  InterviewStatus,
  "default" | "secondary" | "success" | "warning" | "destructive"
> = {
  SCHEDULED: "secondary",
  IN_PROGRESS: "warning",
  COMPLETED: "success",
  ABANDONED: "destructive",
};

export function InterviewListItem({
  interview,
}: {
  interview: InterviewSummary;
}) {
  const link =
    interview.status === "COMPLETED"
      ? `/interview/${interview.id}/result`
      : `/interview/${interview.id}`;

  return (
    <Link href={link}>
      <Card className="transition-colors hover:bg-accent">
        <CardContent className="flex items-center justify-between p-4">
          <div className="min-w-0">
            <p className="truncate font-medium">
              {interview.type.replace("_", " ")} ·{" "}
              {interview.difficulty.toLowerCase()}
            </p>
            <p className="text-xs text-muted-foreground">
              {interview.mode} · {interview.totalQuestionsAsked} questions ·{" "}
              {Math.round(interview.cumulativeScore)} avg
            </p>
          </div>
          <div className="flex shrink-0 items-center gap-2">
            <Badge variant={statusVariant[interview.status]}>
              {interview.status}
            </Badge>
            <ArrowRight className="h-4 w-4 text-muted-foreground" />
          </div>
        </CardContent>
      </Card>
    </Link>
  );
}