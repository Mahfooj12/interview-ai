"use client";

import { useState } from "react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import {
  useAdminUsers,
  useUpdateAdminUser,
} from "@/hooks/useAdmin";
import type { AdminUser } from "@/lib/api/admin";

export function UsersTable() {
  const { data, isLoading } = useAdminUsers();
  const update = useUpdateAdminUser();
  const [pendingId, setPendingId] = useState<string | null>(null);

  const handleToggleStatus = async (user: AdminUser) => {
    setPendingId(user.id);
    try {
      await update.mutateAsync({
        id: user.id,
        payload: {
          status: user.status === "ACTIVE" ? "SUSPENDED" : "ACTIVE",
        },
      });
    } finally {
      setPendingId(null);
    }
  };

  const handlePlanChange = async (user: AdminUser, plan: string) => {
    setPendingId(user.id);
    try {
      await update.mutateAsync({ id: user.id, payload: { status: user.status, plan } });
    } finally {
      setPendingId(null);
    }
  };

  if (isLoading) {
    return (
      <div className="space-y-3">
        <Skeleton className="h-14 w-full" />
        <Skeleton className="h-14 w-full" />
        <Skeleton className="h-14 w-full" />
      </div>
    );
  }

  if (!data || data.length === 0) {
    return <p className="text-sm text-muted-foreground">No users.</p>;
  }

  return (
    <Card>
      <CardContent className="p-0">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="border-b bg-muted/40">
              <tr className="text-left">
                <th className="p-3 font-medium">User</th>
                <th className="p-3 font-medium">Roles</th>
                <th className="p-3 font-medium">Plan</th>
                <th className="p-3 font-medium">Status</th>
                <th className="p-3 font-medium">Interviews</th>
                <th className="p-3 font-medium">Reports</th>
                <th className="p-3 font-medium text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {data.map((user) => (
                <tr key={user.id} className="border-b last:border-b-0">
                  <td className="p-3">
                    <div className="font-medium">{user.name}</div>
                    <div className="text-xs text-muted-foreground">
                      {user.email}
                    </div>
                  </td>
                  <td className="p-3">
                    <div className="flex flex-wrap gap-1">
                      {user.roles.map((r) => (
                        <Badge key={r} variant="outline">
                          {r}
                        </Badge>
                      ))}
                    </div>
                  </td>
                  <td className="p-3">
                    <select
                      className="rounded-md border bg-background px-2 py-1 text-xs"
                      value={user.plan}
                      disabled={pendingId === user.id}
                      onChange={(e) => handlePlanChange(user, e.target.value)}
                    >
                      <option value="FREE">FREE</option>
                      <option value="PRO">PRO</option>
                      <option value="TEAM">TEAM</option>
                    </select>
                  </td>
                  <td className="p-3">
                    <Badge
                      variant={user.status === "ACTIVE" ? "success" : "destructive"}
                    >
                      {user.status}
                    </Badge>
                  </td>
                  <td className="p-3">{user.interviewCount}</td>
                  <td className="p-3">{user.reportCount}</td>
                  <td className="p-3 text-right">
                    <Button
                      size="sm"
                      variant="outline"
                      disabled={pendingId === user.id}
                      onClick={() => handleToggleStatus(user)}
                    >
                      {user.status === "ACTIVE" ? "Suspend" : "Reactivate"}
                    </Button>
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