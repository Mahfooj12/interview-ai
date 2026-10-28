"use client";

import { useState } from "react";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Pagination } from "@/components/ui/pagination";
import { useAdminAiUsage } from "@/hooks/useAdmin";

const PAGE_SIZE = 50;

export function AiUsageTable() {
  const [page, setPage] = useState(0);
  const { data, isLoading, isPlaceholderData } = useAdminAiUsage(
    page,
    PAGE_SIZE,
  );

  if (isLoading) {
    return (
      <div className="space-y-3">
        <Skeleton className="h-14 w-full" />
        <Skeleton className="h-14 w-full" />
      </div>
    );
  }

  if (!data || data.content.length === 0) {
    return (
      <p className="text-sm text-muted-foreground">
        No AI usage recorded.
      </p>
    );
  }

  return (
    <div className={isPlaceholderData ? "opacity-60 transition-opacity" : ""}>
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
                {data.content.map((u) => (
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