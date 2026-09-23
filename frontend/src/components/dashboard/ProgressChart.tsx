"use client";

import {
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import type { InterviewSummary } from "@/types/interview";

interface ProgressChartProps {
  interviews: InterviewSummary[];
}

export function ProgressChart({ interviews }: ProgressChartProps) {
  const data = interviews
    .filter((i) => i.status === "COMPLETED" && i.completedAt)
    .sort(
      (a, b) =>
        new Date(a.completedAt as string).getTime() -
        new Date(b.completedAt as string).getTime(),
    )
    .map((i) => ({
      date: new Date(i.completedAt as string).toLocaleDateString(undefined, {
        month: "short",
        day: "numeric",
      }),
      score: Math.round(i.cumulativeScore),
    }));

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Progress over time</CardTitle>
      </CardHeader>
      <CardContent className="h-64">
        {data.length === 0 ? (
          <div className="flex h-full items-center justify-center text-sm text-muted-foreground">
            Complete an interview to see progress.
          </div>
        ) : (
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={data}>
              <CartesianGrid strokeDasharray="3 3" className="stroke-border" />
              <XAxis dataKey="date" className="text-xs" />
              <YAxis domain={[0, 100]} className="text-xs" />
              <Tooltip />
              <Line
                type="monotone"
                dataKey="score"
                stroke="hsl(var(--primary))"
                strokeWidth={2}
                dot={{ r: 3 }}
              />
            </LineChart>
          </ResponsiveContainer>
        )}
      </CardContent>
    </Card>
  );
}