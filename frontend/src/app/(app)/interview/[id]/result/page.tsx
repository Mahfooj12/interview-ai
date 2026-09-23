"use client";

import { useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { reportApi } from "@/lib/api/report";
import { Skeleton } from "@/components/ui/skeleton";
import { Button } from "@/components/ui/button";

export default function InterviewResultRedirectPage() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const interviewId = params.id;

  const { data, isLoading, isError } = useQuery({
    queryKey: ["reports", "by-interview", interviewId],
    queryFn: () => reportApi.getByInterview(interviewId),
    retry: 2,
    retryDelay: 1500,
  });

  useEffect(() => {
    if (data?.id) {
      router.replace(`/reports/${data.id}`);
    }
  }, [data?.id, router]);

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-16 w-full" />
        <Skeleton className="h-40 w-full" />
        <Skeleton className="h-40 w-full" />
        <p className="text-center text-sm text-muted-foreground">
          Generating your report...
        </p>
      </div>
    );
  }

  if (isError || !data) {
    return (
      <div className="flex flex-col items-center justify-center gap-4 py-16">
        <p className="text-sm text-muted-foreground">
          Report is still generating. This can take a few seconds.
        </p>
        <div className="flex gap-2">
          <Button onClick={() => router.refresh()}>Refresh</Button>
          <Button
            variant="outline"
            onClick={() => router.push("/dashboard")}
          >
            Back to dashboard
          </Button>
        </div>
      </div>
    );
  }

  return null;
}