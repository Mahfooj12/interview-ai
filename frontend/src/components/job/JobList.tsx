"use client";

import { useState } from "react";
import { useDeleteJob, useJobs } from "@/hooks/useJobs";
import { Skeleton } from "@/components/ui/skeleton";
import { Pagination } from "@/components/ui/pagination";
import { JobCard } from "./JobCard";

const PAGE_SIZE = 20;

export function JobList() {
  const [page, setPage] = useState(0);
  const { data, isLoading, isPlaceholderData } = useJobs(page, PAGE_SIZE);
  const del = useDeleteJob();

  if (isLoading) {
    return (
      <div className="space-y-3">
        <Skeleton className="h-20 w-full" />
        <Skeleton className="h-20 w-full" />
      </div>
    );
  }

  if (!data || data.content.length === 0) {
    return (
      <p className="text-sm text-muted-foreground">
        No job descriptions yet.
      </p>
    );
  }

  return (
    <div className={isPlaceholderData ? "opacity-60 transition-opacity" : ""}>
      <div className="space-y-3">
        {data.content.map((job) => (
          <JobCard key={job.id} job={job} onDelete={(id) => del.mutate(id)} />
        ))}
      </div>

      <Pagination
        page={data.page}
        totalPages={data.totalPages}
        totalElements={data.totalElements}
        size={data.size}
        onPageChange={setPage}
      />
    </div>
  );
}