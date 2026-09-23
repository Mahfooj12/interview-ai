"use client";

import Link from "next/link";
import { ArrowRight } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useReports } from "@/hooks/useReports";

export function ReportList() {
  const { data, isLoading } = useReports();

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
        No reports yet. Complete an interview to see your performance report.
      </p>
    );
  }

  return (
    <div className="space-y-3">
      {data.map((report) => (
        <Link key={report.id} href={`/reports/${report.id}`}>
          <Card className="transition-colors hover:bg-accent">
            <CardContent className="flex items-center justify-between p-4">
              <div className="min-w-0">
                <p className="truncate font-medium">
                  Report ·{" "}
                  {new Date(report.generatedAt).toLocaleDateString()}
                </p>
                <p className="text-xs text-muted-foreground">
                  Technical {Math.round(report.technical)} · Communication{" "}
                  {Math.round(report.communication)} · Confidence{" "}
                  {Math.round(report.confidence)}
                </p>
              </div>
              <div className="flex shrink-0 items-center gap-2">
                <Badge variant={scoreVariant(report.overallScore)}>
                  {Math.round(report.overallScore)}/100
                </Badge>
                <ArrowRight className="h-4 w-4 text-muted-foreground" />
              </div>
            </CardContent>
          </Card>
        </Link>
      ))}
    </div>
  );
}

function scoreVariant(score: number): "success" | "warning" | "destructive" {
  if (score >= 75) return "success";
  if (score >= 55) return "warning";
  return "destructive";
}