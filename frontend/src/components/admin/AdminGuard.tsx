"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/stores/auth-store";
import { Spinner } from "@/components/ui/spinner";

export function AdminGuard({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const { user, hydrated } = useAuthStore();

  const isAdmin = user?.roles?.includes("ADMIN") ?? false;

  useEffect(() => {
    if (hydrated && !isAdmin) {
      router.replace("/dashboard");
    }
  }, [hydrated, isAdmin, router]);

  if (!hydrated) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <Spinner className="h-6 w-6" />
      </div>
    );
  }

  if (!isAdmin) return null;

  return <>{children}</>;
}