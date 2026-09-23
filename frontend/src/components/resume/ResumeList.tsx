"use client";

import {
  useAnalyzeResume,
  useDeleteResume,
  useResumes,
  useSetPrimaryResume,
} from "@/hooks/useResumes";
import { Skeleton } from "@/components/ui/skeleton";
import { ResumeCard } from "./ResumeCard";

export function ResumeList() {
  const { data, isLoading } = useResumes();
  const analyze = useAnalyzeResume();
  const setPrimary = useSetPrimaryResume();
  const del = useDeleteResume();

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
        No resumes uploaded yet.
      </p>
    );
  }

  return (
    <div className="space-y-3">
      {data.map((resume) => (
        <ResumeCard
          key={resume.id}
          resume={resume}
          analyzing={analyze.isPending && analyze.variables?.id === resume.id}
          onAnalyze={(id) => analyze.mutate({ id, force: true })}
          onSetPrimary={(id) => setPrimary.mutate(id)}
          onDelete={(id) => del.mutate(id)}
        />
      ))}
    </div>
  );
}