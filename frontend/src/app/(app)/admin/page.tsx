"use client";

import { useAdminStatistics } from "@/hooks/useAdmin";
import { Skeleton } from "@/components/ui/skeleton";
import { StatisticsCards } from "@/components/admin/StatisticsCards";

export default function AdminStatisticsPage() {
  const { data, isLoading } = useAdminStatistics();

  if (isLoading || !data) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-24 w-full" />
        <Skeleton className="h-24 w-full" />
      </div>
    );
  }

  return <StatisticsCards data={data} />;
}