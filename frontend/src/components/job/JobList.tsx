"use client";

import { useDeleteJob, useJobs } from "@/hooks/useJobs";
import { Skeleton } from "@/components/ui/skeleton";
import { JobCard } from "./JobCard";

export function JobList() {
  const { data, isLoading } = useJobs();
  const del = useDeleteJob();

  if (isLoading) {
    return (
      <div className="space-y-3">
        <Skeleton className="h-20 w-full" />
        <Skeleton className="h-20 w-full" />
      </div>
    );
  }

  if (!data || data.length === 0) {
    return (
      <p className="text-sm text-muted-foreground">
        No job descriptions yet.
      </p>
    );
  }

  return (
    <div className="space-y-3">
      {data.map((job) => (
        <JobCard key={job.id} job={job} onDelete={(id) => del.mutate(id)} />
      ))}
    </div>
  );
}