"use client";

import { useProgress, useProgressSkills } from "@/hooks/useReports";
import { Skeleton } from "@/components/ui/skeleton";
import { ProgressOverview } from "@/components/progress/ProgressOverview";
import { ScoreTrend } from "@/components/progress/ScoreTrend";
import { SkillRadar } from "@/components/progress/SkillRadar";
import { WeakAreaLeaderboard } from "@/components/progress/WeakAreaLeaderboard";

export default function ProgressPage() {
  const { data: summary, isLoading: loadingSummary } = useProgress();
  const { data: skills, isLoading: loadingSkills } = useProgressSkills();

  if (loadingSummary || loadingSkills || !summary) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-24 w-full" />
        <Skeleton className="h-72 w-full" />
        <Skeleton className="h-72 w-full" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold">Progress</h1>
        <p className="text-muted-foreground">
          Track your improvement across interviews and skills.
        </p>
      </div>

      <ProgressOverview data={summary} />

      <div className="grid gap-4 lg:grid-cols-2">
        <ScoreTrend timeline={summary.timeline} />
        <SkillRadar skills={skills ?? []} />
      </div>

      <WeakAreaLeaderboard
        weakAreas={summary.topWeakAreas}
        strongAreas={summary.topStrongAreas}
      />
    </div>
  );
}