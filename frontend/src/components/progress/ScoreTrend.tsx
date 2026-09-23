"use client";

import {
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import type { ProgressPoint } from "@/types/report";

export function ScoreTrend({ timeline }: { timeline: ProgressPoint[] }) {
  const data = timeline.map((p) => ({
    date: new Date(p.date).toLocaleDateString(undefined, {
      month: "short",
      day: "numeric",
    }),
    overall: Math.round(p.overallScore),
    technical: Math.round(p.technicalScore),
    communication: Math.round(p.communicationScore),
  }));

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Score trend</CardTitle>
      </CardHeader>
      <CardContent className="h-72">
        {data.length === 0 ? (
          <div className="flex h-full items-center justify-center text-sm text-muted-foreground">
            No data yet.
          </div>
        ) : (
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={data}>
              <CartesianGrid strokeDasharray="3 3" className="stroke-border" />
              <XAxis dataKey="date" className="text-xs" />
              <YAxis domain={[0, 100]} className="text-xs" />
              <Tooltip />
              <Legend />
              <Line
                type="monotone"
                dataKey="overall"
                stroke="hsl(var(--primary))"
                strokeWidth={2}
              />
              <Line
                type="monotone"
                dataKey="technical"
                stroke="#10b981"
                strokeWidth={2}
              />
              <Line
                type="monotone"
                dataKey="communication"
                stroke="#f59e0b"
                strokeWidth={2}
              />
            </LineChart>
          </ResponsiveContainer>
        )}
      </CardContent>
    </Card>
  );
}