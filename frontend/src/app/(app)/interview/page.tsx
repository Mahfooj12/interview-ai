"use client";

import Link from "next/link";
import { Button } from "@/components/ui/button";
import { InterviewListItem } from "@/components/interview/InterviewListItem";
import { useInterviewsPaginated } from "@/hooks/useInterviews";
import { Skeleton } from "@/components/ui/skeleton";

export default function InterviewsPage() {
  const {
    data,
    isLoading,
    isFetchingNextPage,
    hasNextPage,
    fetchNextPage,
  } = useInterviewsPaginated(20);

  const allInterviews = data?.pages.flatMap((p) => p.items) ?? [];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold">Interviews</h1>
          <p className="text-muted-foreground">
            Review your past sessions or start a new one.
          </p>
        </div>
        <Button asChild>
          <Link href="/interview/setup">New interview</Link>
        </Button>
      </div>

      {isLoading ? (
        <div className="space-y-3">
          <Skeleton className="h-16 w-full" />
          <Skeleton className="h-16 w-full" />
          <Skeleton className="h-16 w-full" />
        </div>
      ) : allInterviews.length === 0 ? (
        <p className="text-sm text-muted-foreground">No interviews yet.</p>
      ) : (
        <>
          <div className="space-y-3">
            {allInterviews.map((interview) => (
              <InterviewListItem
                key={interview.id}
                interview={interview}
              />
            ))}
          </div>

          <div className="flex justify-center pt-4">
            {hasNextPage ? (
              <Button
                variant="outline"
                onClick={() => fetchNextPage()}
                disabled={isFetchingNextPage}
              >
                {isFetchingNextPage ? "Loading..." : "Load more"}
              </Button>
            ) : (
              <p className="text-xs text-muted-foreground">
                End of list
              </p>
            )}
          </div>
        </>
      )}
    </div>
  );
}