"use client";

import { useState } from "react";
import Link from "next/link";
import { ArrowRight } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Pagination } from "@/components/ui/pagination";
import { useReports } from "@/hooks/useReports";

const PAGE_SIZE = 20;

export function ReportList() {
  const [page, setPage] = useState(0);
  const { data, isLoading, isPlaceholderData } = useReports(page, PAGE_SIZE);

  if (isLoading) {
    return (
      <div className="space-y-3">
        <Skeleton className="h-16 w-full" />
        <Skeleton className="h-16 w-full" />
      </div>
    );
  }

  if (!data || data.content.length === 0) {
    return (
      <p className="text-sm text-muted-foreground">
        No reports yet. Complete an interview to see your performance report.
      </p>
    );
  }

  return (
    <div className={isPlaceholderData ? "opacity-60 transition-opacity" : ""}>
      <div className="space-y-3">
        {data.content.map((report) => (
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

function scoreVariant(score: number): "success" | "warning" | "destructive" {
  if (score >= 75) return "success";
  if (score >= 55) return "warning";
  return "destructive";
}