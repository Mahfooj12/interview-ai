"use client";

import Link from "next/link";
import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { PlusCircle } from "lucide-react";

import { authApi } from "@/lib/api/auth";
import { useInterviews } from "@/hooks/useInterviews";
import { useResumes } from "@/hooks/useResumes";
import { useJobs } from "@/hooks/useJobs";
import { useProgress } from "@/hooks/useReports";

import { Button } from "@/components/ui/button";
import { StatCard } from "@/components/dashboard/StatCard";
import { ProgressChart } from "@/components/dashboard/ProgressChart";
import { RecentInterviews } from "@/components/dashboard/RecentInterviews";
import { WeakAreasCard } from "@/components/dashboard/WeakAreasCard";
import { StrongAreasCard } from "@/components/dashboard/StrongAreasCard";
import { Skeleton } from "@/components/ui/skeleton";

export default function DashboardPage() {
  const { data: me, isLoading: loadingMe } = useQuery({
    queryKey: ["auth", "me"],
    queryFn: authApi.me,
  });
  const { data: interviews, isLoading: loadingInterviews } = useInterviews();
  const { data: resumes } = useResumes();
  const { data: jobs } = useJobs();
  const { data: progress } = useProgress();

  const stats = useMemo(() => {
    const completed = (interviews ?? []).filter(
      (i) => i.status === "COMPLETED"
    );
    const total = completed.length;
    const avg =
      total === 0
        ? 0
        : Math.round(
            completed.reduce((sum, i) => sum + i.cumulativeScore, 0) / total
          );
    const best =
      total === 0
        ? 0
        : Math.round(Math.max(...completed.map((i) => i.cumulativeScore)));
    return { total, avg, best };
  }, [interviews]);

  // Strong/weak areas come from the aggregated /api/progress endpoint
  const strongAreas = progress?.topStrongAreas ?? [];
  const weakAreas = progress?.topWeakAreas ?? [];

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-semibold">
            {loadingMe ? (
              <Skeleton className="h-8 w-56" />
            ) : (
              <>Welcome{me?.name ? `, ${me.name}` : ""} 👋</>
            )}
          </h1>
          <p className="text-muted-foreground">
            Track your interview performance and improve with AI feedback.
          </p>
        </div>
        <Button asChild>
          <Link href="/interview/setup">
            <PlusCircle className="mr-2 h-4 w-4" />
            New interview
          </Link>
        </Button>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard label="Interviews completed" value={stats.total} />
        <StatCard
          label="Average score"
          value={`${stats.avg}/100`}
          accent="success"
        />
        <StatCard
          label="Best score"
          value={`${stats.best}/100`}
          accent="warning"
        />
        <StatCard
          label="Resumes"
          value={resumes?.length ?? 0}
          hint={`${jobs?.length ?? 0} job descriptions`}
        />
      </div>

      <div className="grid gap-4 lg:grid-cols-3">
        <div className="lg:col-span-2">
          <ProgressChart interviews={interviews ?? []} />
        </div>
        <RecentInterviews />
      </div>

      <div className="grid gap-4 lg:grid-cols-2">
        <StrongAreasCard areas={strongAreas} />
        <WeakAreasCard areas={weakAreas} />
      </div>

      {loadingInterviews ? <Skeleton className="h-40 w-full" /> : null}
    </div>
  );
}