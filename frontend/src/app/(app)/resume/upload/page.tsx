"use client";

import { useState } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { ResumeUploadForm } from "@/components/resume/ResumeUploadForm";
import { ParsedResumePreview } from "@/components/resume/ParsedResumePreview";
import { useAnalyzeResume, useResume } from "@/hooks/useResumes";
import { Skeleton } from "@/components/ui/skeleton";
import type { ParsedResume } from "@/types/resume";

export default function ResumeUploadPage() {
  const [resumeId, setResumeId] = useState<string | null>(null);
  const [parsed, setParsed] = useState<ParsedResume | null>(null);
  const analyze = useAnalyzeResume();
  const resume = useResume(resumeId ?? undefined);

  const handleAnalyze = async () => {
    if (!resumeId) return;
    const result = await analyze.mutateAsync({ id: resumeId, force: true });
    if (result.parsed) setParsed(result.parsed);
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold">Upload resume</h1>
          <p className="text-muted-foreground">
            We extract skills, projects, experience and technical claims.
          </p>
        </div>
        <Button asChild variant="ghost">
          <Link href="/resume">Back</Link>
        </Button>
      </div>

      <ResumeUploadForm
        onUploaded={(id) => setResumeId(id)}
      />

      {resumeId ? (
        <div className="flex items-center justify-between rounded-md border p-3">
          <p className="text-sm text-muted-foreground">
            Resume uploaded. Run AI analysis to extract structured data.
          </p>
          <Button
            onClick={handleAnalyze}
            disabled={analyze.isPending}
          >
            {analyze.isPending ? "Analyzing…" : "Analyze"}
          </Button>
        </div>
      ) : null}

      {analyze.isPending || resume.isLoading ? (
        <Skeleton className="h-40 w-full" />
      ) : null}

      {parsed ? <ParsedResumePreview parsed={parsed} /> : null}
    </div>
  );
}