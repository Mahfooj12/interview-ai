"use client";

import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useAdminAiUsage } from "@/hooks/useAdmin";

export function AiUsageTable() {
  const { data, isLoading } = useAdminAiUsage(200);

  if (isLoading) {
    return (
      <div className="space-y-3">
        <Skeleton className="h-14 w-full" />
        <Skeleton className="h-14 w-full" />
      </div>
    );
  }

  if (!data || data.length === 0) {
    return <p className="text-sm text-muted-foreground">No AI usage recorded.</p>;
  }

  return (
    <Card>
      <CardContent className="p-0">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="border-b bg-muted/40">
              <tr className="text-left">
                <th className="p-3 font-medium">When</th>
                <th className="p-3 font-medium">User</th>
                <th className="p-3 font-medium">Operation</th>
                <th className="p-3 font-medium">Model</th>
                <th className="p-3 font-medium">Tokens</th>
                <th className="p-3 font-medium">Latency</th>
                <th className="p-3 font-medium">Status</th>
              </tr>
            </thead>
            <tbody>
              {data.map((u) => (
                <tr key={u.id} className="border-b last:border-b-0">
                  <td className="p-3 text-xs text-muted-foreground">
                    {new Date(u.createdAt).toLocaleString()}
                  </td>
                  <td className="p-3 font-mono text-xs">{u.userId}</td>
                  <td className="p-3">{u.operation}</td>
                  <td className="p-3">{u.model}</td>
                  <td className="p-3">{u.totalTokens}</td>
                  <td className="p-3">{u.latencyMs} ms</td>
                  <td className="p-3">
                    <Badge variant={u.success ? "success" : "destructive"}>
                      {u.success ? "OK" : "FAILED"}
                    </Badge>
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