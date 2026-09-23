"use client";

import { useQuery } from "@tanstack/react-query";
import { LogOut, Mail, Shield, User as UserIcon } from "lucide-react";
import { useRouter } from "next/navigation";
import { toast } from "sonner";

import { authApi } from "@/lib/api/auth";
import { tokenStore } from "@/lib/auth/token-store";
import { useAuthStore } from "@/stores/auth-store";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Skeleton } from "@/components/ui/skeleton";

export default function ProfilePage() {
  const router = useRouter();
  const { user, clear } = useAuthStore();
  const { data: me, isLoading } = useQuery({
    queryKey: ["auth", "me"],
    queryFn: authApi.me,
  });

  const profile = me ?? user;

  const handleLogout = async () => {
    const refreshToken = tokenStore.getRefreshToken();
    try {
      if (refreshToken) await authApi.logout(refreshToken);
    } catch {
      // ignore
    } finally {
      clear();
      toast.success("Logged out");
      router.push("/login");
    }
  };

  return (
    <div className="mx-auto max-w-2xl space-y-6">
      <div>
        <h1 className="text-2xl font-semibold">Profile</h1>
        <p className="text-muted-foreground">
          Your account details and preferences.
        </p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Account</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {isLoading && !profile ? (
            <Skeleton className="h-20 w-full" />
          ) : (
            <>
              <div className="flex items-center gap-3">
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary/10">
                  <UserIcon className="h-6 w-6 text-primary" />
                </div>
                <div>
                  <p className="font-medium">{profile?.name ?? "—"}</p>
                  <p className="text-sm text-muted-foreground">
                    {profile?.email ?? "—"}
                  </p>
                </div>
              </div>

              <Separator />

              <div className="grid gap-3 sm:grid-cols-2">
                <div>
                  <p className="text-xs text-muted-foreground">Plan</p>
                  <div className="mt-1">
                    <Badge variant="secondary">{profile?.plan ?? "FREE"}</Badge>
                  </div>
                </div>
                <div>
                  <p className="text-xs text-muted-foreground">Status</p>
                  <div className="mt-1">
                    <Badge
                      variant={
                        profile?.status === "ACTIVE" ? "success" : "destructive"
                      }
                    >
                      {profile?.status ?? "ACTIVE"}
                    </Badge>
                  </div>
                </div>
                <div>
                  <p className="text-xs text-muted-foreground">Roles</p>
                  <div className="mt-1 flex flex-wrap gap-1">
                    {(profile?.roles ?? ["USER"]).map((r) => (
                      <Badge key={r} variant="outline">
                        <Shield className="mr-1 h-3 w-3" />
                        {r}
                      </Badge>
                    ))}
                  </div>
                </div>
                <div>
                  <p className="text-xs text-muted-foreground">Member since</p>
                  <p className="mt-1 text-sm">
                    {profile?.createdAt
                      ? new Date(profile.createdAt).toLocaleDateString()
                      : "—"}
                  </p>
                </div>
              </div>
            </>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Session</CardTitle>
        </CardHeader>
        <CardContent className="space-y-3">
          <p className="text-sm text-muted-foreground">
            Log out of your account on this device.
          </p>
          <Button variant="destructive" onClick={handleLogout}>
            <LogOut className="mr-2 h-4 w-4" />
            Log out
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}