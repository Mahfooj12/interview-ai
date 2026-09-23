"use client";

import { FileText, Star, Trash2, Sparkles } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import type { ResumeSummary } from "@/types/resume";

interface ResumeCardProps {
  resume: ResumeSummary;
  onAnalyze: (id: string) => void;
  onSetPrimary: (id: string) => void;
  onDelete: (id: string) => void;
  analyzing?: boolean;
}

export function ResumeCard({
  resume,
  onAnalyze,
  onSetPrimary,
  onDelete,
  analyzing,
}: ResumeCardProps) {
  return (
    <Card>
      <CardContent className="flex flex-col gap-3 p-4 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex min-w-0 items-start gap-3">
          <FileText className="mt-1 h-5 w-5 shrink-0 text-muted-foreground" />
          <div className="min-w-0">
            <p className="truncate font-medium">{resume.fileName}</p>
            <p className="text-xs text-muted-foreground">
              {resume.parsedName ?? "—"} · {resume.skillCount} skills ·{" "}
              {resume.projectCount} projects
            </p>
            <div className="mt-1 flex flex-wrap gap-1">
              {resume.primaryResume ? (
                <Badge variant="success">Primary</Badge>
              ) : null}
              <Badge variant="outline">{resume.fileType}</Badge>
            </div>
          </div>
        </div>

        <div className="flex shrink-0 flex-wrap gap-2">
          <Button
            size="sm"
            variant="outline"
            onClick={() => onAnalyze(resume.id)}
            disabled={analyzing}
          >
            <Sparkles className="mr-1 h-4 w-4" />
            {analyzing ? "Analyzing…" : "Analyze"}
          </Button>
          {!resume.primaryResume ? (
            <Button
              size="sm"
              variant="ghost"
              onClick={() => onSetPrimary(resume.id)}
            >
              <Star className="mr-1 h-4 w-4" />
              Set primary
            </Button>
          ) : null}
          <Button
            size="sm"
            variant="ghost"
            className="text-destructive hover:text-destructive"
            onClick={() => onDelete(resume.id)}
          >
            <Trash2 className="h-4 w-4" />
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}