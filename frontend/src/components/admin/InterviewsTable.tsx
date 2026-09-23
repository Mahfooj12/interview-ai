"use client";

import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useAdminInterviews } from "@/hooks/useAdmin";
import type { InterviewStatus } from "@/types/interview";

const statusVariant: Record<
  InterviewStatus,
  "default" | "secondary" | "success" | "warning" | "destructive"
> = {
  SCHEDULED: "secondary",
  IN_PROGRESS: "warning",
  COMPLETED: "success",
  ABANDONED: "destructive",
};

export function InterviewsTable() {
  const { data, isLoading } = useAdminInterviews(100);

  if (isLoading) {
    return (
      <div className="space-y-3">
        <Skeleton className="h-14 w-full" />
        <Skeleton className="h-14 w-full" />
      </div>
    );
  }

  if (!data || data.length === 0) {
    return <p className="text-sm text-muted-foreground">No interviews yet.</p>;
  }

  return (
    <Card>
      <CardContent className="p-0">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="border-b bg-muted/40">
              <tr className="text-left">
                <th className="p-3 font-medium">User</th>
                <th className="p-3 font-medium">Type</th>
                <th className="p-3 font-medium">Mode</th>
                <th className="p-3 font-medium">Difficulty</th>
                <th className="p-3 font-medium">Questions</th>
                <th className="p-3 font-medium">Avg score</th>
                <th className="p-3 font-medium">Status</th>
                <th className="p-3 font-medium">Started</th>
              </tr>
            </thead>
            <tbody>
              {data.map((i) => (
                <tr key={i.id} className="border-b last:border-b-0">
                  <td className="p-3">{i.userEmail ?? i.userId}</td>
                  <td className="p-3">{i.type.replace("_", " ")}</td>
                  <td className="p-3">{i.mode}</td>
                  <td className="p-3">{i.difficulty}</td>
                  <td className="p-3">{i.totalQuestionsAsked}</td>
                  <td className="p-3">{Math.round(i.cumulativeScore)}</td>
                  <td className="p-3">
                    <Badge variant={statusVariant[i.status]}>{i.status}</Badge>
                  </td>
                  <td className="p-3 text-xs text-muted-foreground">
                    {i.startedAt ? new Date(i.startedAt).toLocaleString() : "—"}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </CardContent>
    </Card>
  );
}