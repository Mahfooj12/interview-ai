"use client";

import { StatCard } from "@/components/dashboard/StatCard";
import type { ProgressSummary } from "@/types/report";

export function ProgressOverview({ data }: { data: ProgressSummary }) {
  return (
    <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
      <StatCard label="Total interviews" value={data.totalInterviews} />
      <StatCard
        label="Average score"
        value={`${Math.round(data.averageScore)}/100`}
        accent="success"
      />
      <StatCard
        label="Best score"
        value={`${Math.round(data.bestScore)}/100`}
        accent="warning"
      />
      <StatCard
        label="Completed"
        value={data.completedInterviews}
      />
    </div>
  );
}