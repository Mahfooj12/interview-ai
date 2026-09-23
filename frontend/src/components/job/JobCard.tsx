"use client";

import Link from "next/link";
import { Briefcase, Trash2 } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import type { JobDescriptionSummary } from "@/types/job";

interface JobCardProps {
  job: JobDescriptionSummary;
  onDelete: (id: string) => void;
}

export function JobCard({ job, onDelete }: JobCardProps) {
  return (
    <Card>
      <CardContent className="flex flex-col gap-3 p-4 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex min-w-0 items-start gap-3">
          <Briefcase className="mt-1 h-5 w-5 shrink-0 text-muted-foreground" />
          <div className="min-w-0">
            <p className="truncate font-medium">
              {job.title ?? "Untitled role"}
            </p>
            <p className="text-xs text-muted-foreground">
              {job.company ?? "—"} · {job.requiredSkillCount} required skills
            </p>
            <div className="mt-1 flex flex-wrap gap-1">
              {job.seniority ? (
                <Badge variant="outline">{job.seniority}</Badge>
              ) : null}
              {typeof job.matchPercentage === "number" ? (
                <Badge variant="success">
                  {Math.round(job.matchPercentage)}% match
                </Badge>
              ) : null}
            </div>
          </div>
        </div>

        <div className="flex shrink-0 gap-2">
          <Button size="sm" variant="outline" asChild>
            <Link href={`/jobs/${job.id}`}>View</Link>
          </Button>
          <Button
            size="sm"
            variant="ghost"
            className="text-destructive hover:text-destructive"
            onClick={() => onDelete(job.id)}
          >
            <Trash2 className="h-4 w-4" />
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}