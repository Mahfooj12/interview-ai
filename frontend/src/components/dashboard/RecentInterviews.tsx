"use client";

import Link from "next/link";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { useInterviews } from "@/hooks/useInterviews";
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

/**
 * Returns the correct route for an interview based on its status and mode.
 *
 * - COMPLETED → /interview/{id}/result
 * - VOICE     → /interview/{id}/voice
 * - VIDEO     → /interview/{id}/video
 * - TEXT      → /interview/{id}
 */
function getInterviewUrl(interview: InterviewSummary): string {
  if (interview.status === "COMPLETED") {
    return `/interview/${interview.id}/result`;
  }
  const mode = (interview.mode ?? "TEXT").toLowerCase();
  if (mode === "voice") return `/interview/${interview.id}/voice`;
  if (mode === "video") return `/interview/${interview.id}/video`;
  return `/interview/${interview.id}`;
}

export function RecentInterviews() {
  const { data, isLoading } = useInterviews();

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Recent interviews</CardTitle>
      </CardHeader>
      <CardContent className="space-y-3">
        {isLoading ? (
          <>
            <Skeleton className="h-12 w-full" />
            <Skeleton className="h-12 w-full" />
          </>
        ) : !data || data.length === 0 ? (
          <p className="text-sm text-muted-foreground">
            No interviews yet.{" "}
            <Link
              href="/interview/setup"
              className="text-primary underline-offset-4 hover:underline"
            >
              Start one
            </Link>
            .
          </p>
        ) : (
          data.slice(0, 5).map((interview) => (
            <Link
              key={interview.id}
              href={getInterviewUrl(interview)}
              className="flex items-center justify-between rounded-md border p-3 transition-colors hover:bg-accent"
            >
              <div className="min-w-0">
                <p className="truncate text-sm font-medium">
                  {interview.type.replace("_", " ")} · {interview.mode} ·{" "}
                  {interview.difficulty.toLowerCase()}
                </p>
                <p className="text-xs text-muted-foreground">
                  {interview.totalQuestionsAsked} questions ·{" "}
                  {Math.round(interview.cumulativeScore)} avg
                </p>
              </div>
              <Badge variant={statusVariant[interview.status]}>
                {interview.status}
              </Badge>
            </Link>
          ))
        )}
      </CardContent>
    </Card>
  );
}