"use client";

import { StatCard } from "@/components/dashboard/StatCard";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import type { AdminStatistics } from "@/lib/api/admin";

export function StatisticsCards({ data }: { data: AdminStatistics }) {
  return (
    <div className="space-y-6">
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard label="Total users" value={data.totalUsers} />
        <StatCard
          label="Active users"
          value={data.activeUsers}
          accent="success"
        />
        <StatCard
          label="Suspended"
          value={data.suspendedUsers}
          accent="destructive"
        />
        <StatCard label="Reports" value={data.totalReports} />
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard label="Total interviews" value={data.totalInterviews} />
        <StatCard
          label="Completed interviews"
          value={data.completedInterviews}
          accent="success"
        />
        <StatCard
          label="In progress"
          value={data.inProgressInterviews}
          accent="warning"
        />
        <StatCard
          label="Avg latency"
          value={`${Math.round(data.averageLatencyMs)} ms`}
        />
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard label="AI calls (24h)" value={data.totalAiCalls24h} />
        <StatCard label="AI calls (7d)" value={data.totalAiCalls7d} />
        <StatCard label="AI calls (30d)" value={data.totalAiCalls30d} />
        <StatCard
          label="Tokens (30d)"
          value={data.totalTokens30d.toLocaleString()}
        />
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Top AI operations (30 days)</CardTitle>
        </CardHeader>
        <CardContent>
          {data.topOperations.length === 0 ? (
            <p className="text-sm text-muted-foreground">No AI calls yet.</p>
          ) : (
            <ul className="space-y-2 text-sm">
              {data.topOperations.map((op) => (
                <li
                  key={op.operation}
                  className="flex items-center justify-between border-b py-1 last:border-b-0"
                >
                  <span className="font-medium">{op.operation}</span>
                  <span className="text-muted-foreground">{op.count}</span>
                </li>
              ))}
            </ul>
          )}
        </CardContent>
      </Card>
    </div>
  );
}