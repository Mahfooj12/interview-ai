"use client";

import Link from "next/link";
import { ArrowLeft, Clock } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import type { ReportInterviewContext } from "@/types/report";

interface ReportHeaderProps {
  title: string;
  context: ReportInterviewContext | null;
  generatedAt: string;
}

export function ReportHeader({ title, context, generatedAt }: ReportHeaderProps) {
  return (
    <div className="flex flex-wrap items-start justify-between gap-3 border-b pb-4">
      <div className="space-y-2">
        <Button asChild variant="ghost" size="sm" className="-ml-2 h-7 px-2">
          <Link href="/reports">
            <ArrowLeft className="mr-1 h-4 w-4" />
            All reports
          </Link>
        </Button>
        <h1 className="text-2xl font-semibold">{title}</h1>
        <div className="flex flex-wrap items-center gap-2 text-xs">
          {context ? (
            <>
              <Badge variant="outline">{context.type.replace("_", " ")}</Badge>
              <Badge variant="outline">{context.difficulty}</Badge>
              <Badge variant="outline">{context.mode}</Badge>
              <span className="flex items-center gap-1 text-muted-foreground">
                <Clock className="h-3 w-3" />
                {formatDuration(context.durationSec)}
              </span>
            </>
          ) : null}
          <span className="text-muted-foreground">
            Generated {new Date(generatedAt).toLocaleString()}
          </span>
        </div>
      </div>
    </div>
  );
}

function formatDuration(seconds: number) {
  if (!seconds) return "—";
  const m = Math.floor(seconds / 60);
  const s = seconds % 60;
  return `${m}m ${s}s`;
}