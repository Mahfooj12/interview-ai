"use client";

import { useInterviews } from "@/hooks/useInterview";
import { Skeleton } from "@/components/ui/skeleton";
import { InterviewListItem } from "./InterviewListItem";

export function InterviewList() {
  const { data, isLoading } = useInterviews();

  if (isLoading) {
    return (
      <div className="space-y-3">
        <Skeleton className="h-16 w-full" />
        <Skeleton className="h-16 w-full" />
      </div>
    );
  }

  if (!data || data.length === 0) {
    return (
      <p className="text-sm text-muted-foreground">
        No interviews yet.
      </p>
    );
  }

  return (
    <div className="space-y-3">
      {data.map((interview) => (
        <InterviewListItem key={interview.id} interview={interview} />
      ))}
    </div>
  );
}