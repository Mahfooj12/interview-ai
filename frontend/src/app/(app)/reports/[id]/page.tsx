"use client";

import { useParams } from "next/navigation";
import { useReport } from "@/hooks/useReports";

import { Skeleton } from "@/components/ui/skeleton";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { ScoreRadial } from "@/components/report/ScoreRadial";
import { ReportHeader } from "@/components/report/ReportHeader";
import { SectionBreakdown } from "@/components/report/SectionBreakdown";
import { StrengthsWeaknesses } from "@/components/report/StrengthsWeaknesses";
import { CommunicationStats } from "@/components/report/CommunicationStats";
import { QuestionFeedbackList } from "@/components/report/QuestionFeedbackList";
import { LearningRoadmap } from "@/components/report/LearningRoadmap";

export default function ReportDetailPage() {
  const params = useParams<{ id: string }>();
  const { data, isLoading, isError } = useReport(params.id);

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-20 w-full" />
        <Skeleton className="h-40 w-full" />
        <Skeleton className="h-40 w-full" />
      </div>
    );
  }

  if (isError || !data) {
    return (
      <p className="text-sm text-muted-foreground">
        Report not found.
      </p>
    );
  }

  return (
    <div className="space-y-6">
      <ReportHeader
        title="Interview report"
        context={null}
        generatedAt={data.generatedAt}
      />

      <div className="grid gap-6 lg:grid-cols-3">
        <Card className="lg:col-span-1">
          <CardHeader>
            <CardTitle className="text-base">Overall</CardTitle>
          </CardHeader>
          <CardContent className="flex flex-col items-center gap-2">
            <ScoreRadial score={data.sections.overall} />
            {data.summary ? (
              <p className="mt-2 text-center text-sm text-muted-foreground">
                {data.summary}
              </p>
            ) : null}
          </CardContent>
        </Card>

        <div className="lg:col-span-2">
          <SectionBreakdown sections={data.sections} />
        </div>
      </div>

      <StrengthsWeaknesses
        strongAreas={data.strongAreas}
        weakAreas={data.weakAreas}
      />

      <div className="grid gap-4 lg:grid-cols-2">
        <CommunicationStats stats={data.communicationStats} />
        <LearningRoadmap
          items={data.learningRoadmap}
          weakAreas={data.weakAreas}
        />
      </div>

      <QuestionFeedbackList feedback={data.questionFeedback} />
    </div>
  );
}