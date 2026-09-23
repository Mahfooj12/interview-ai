"use client";

import { useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import { ArrowLeft, Briefcase, Sparkles } from "lucide-react";

import { useJob, useMatchJob } from "@/hooks/useJobs";
import { useResumes } from "@/hooks/useResumes";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Skeleton } from "@/components/ui/skeleton";
import { JobMatchResult } from "@/components/job/JobMatchResult";

export default function JobDetailPage() {
  const params = useParams<{ id: string }>();
  const jobId = params.id;

  const { data: job, isLoading } = useJob(jobId);
  const { data: resumes } = useResumes();
  const match = useMatchJob();

  const [selectedResumeId, setSelectedResumeId] = useState<string>("");

  const handleMatch = async () => {
    if (!selectedResumeId || !jobId) return;
    await match.mutateAsync({ id: jobId, resumeId: selectedResumeId });
  };

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-16 w-full" />
        <Skeleton className="h-40 w-full" />
      </div>
    );
  }

  if (!job) {
    return (
      <div className="space-y-4">
        <p className="text-sm text-muted-foreground">Job not found.</p>
        <Button asChild variant="outline">
          <Link href="/jobs">
            <ArrowLeft className="mr-2 h-4 w-4" />
            Back to jobs
          </Link>
        </Button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-start justify-between gap-3">
        <div className="space-y-1">
          <Button asChild variant="ghost" size="sm" className="-ml-2 h-7 px-2">
            <Link href="/jobs">
              <ArrowLeft className="mr-1 h-4 w-4" />
              All jobs
            </Link>
          </Button>
          <h1 className="text-2xl font-semibold">
            {job.title ?? "Untitled role"}
          </h1>
          <div className="flex flex-wrap items-center gap-2 text-xs">
            {job.company ? (
              <span className="flex items-center gap-1 text-muted-foreground">
                <Briefcase className="h-3 w-3" />
                {job.company}
              </span>
            ) : null}
            {job.parsed?.seniority ? (
              <Badge variant="outline">{job.parsed.seniority}</Badge>
            ) : null}
          </div>
        </div>
      </div>

      <div className="grid gap-6 lg:grid-cols-3">
        <div className="space-y-4 lg:col-span-2">
          <Card>
            <CardHeader>
              <CardTitle className="text-base">Job description</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="whitespace-pre-wrap text-sm text-muted-foreground">
                {job.rawText || "—"}
              </p>
            </CardContent>
          </Card>

          {job.parsed ? (
            <>
              <ParsedList
                title="Required skills"
                items={job.parsed.requiredSkills}
                variant="default"
              />
              <ParsedList
                title="Preferred skills"
                items={job.parsed.preferredSkills}
                variant="secondary"
              />
              <ParsedList
                title="Technologies"
                items={job.parsed.technologies}
                variant="outline"
              />
              <ParsedList
                title="Responsibilities"
                items={job.parsed.responsibilities}
                variant="outline"
              />
            </>
          ) : null}
        </div>

        <div className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle className="text-base">Match with resume</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              {!resumes || resumes.length === 0 ? (
                <p className="text-sm text-muted-foreground">
                  Upload a resume to compute a match.{" "}
                  <Link
                    href="/resume/upload"
                    className="text-primary underline"
                  >
                    Upload resume
                  </Link>
                </p>
              ) : (
                <>
                  <select
                    className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                    value={selectedResumeId}
                    onChange={(e) => setSelectedResumeId(e.target.value)}
                  >
                    <option value="">— Select a resume —</option>
                    {resumes.map((r) => (
                      <option key={r.id} value={r.id}>
                        {r.fileName}
                        {r.parsedName ? ` (${r.parsedName})` : ""}
                      </option>
                    ))}
                  </select>
                  <Button
                    className="w-full"
                    disabled={!selectedResumeId || match.isPending}
                    onClick={handleMatch}
                  >
                    <Sparkles className="mr-2 h-4 w-4" />
                    {match.isPending ? "Computing..." : "Compute match"}
                  </Button>
                </>
              )}
            </CardContent>
          </Card>

          {job.matchResult ? (
            <JobMatchResult match={job.matchResult} />
          ) : null}
        </div>
      </div>
    </div>
  );
}

function ParsedList({
  title,
  items,
  variant,
}: {
  title: string;
  items: string[] | undefined;
  variant: "default" | "secondary" | "outline";
}) {
  if (!items || items.length === 0) return null;
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">{title}</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="flex flex-wrap gap-2">
          {items.map((item) => (
            <Badge key={item} variant={variant}>
              {item}
            </Badge>
          ))}
        </div>
      </CardContent>
    </Card>
  );
}