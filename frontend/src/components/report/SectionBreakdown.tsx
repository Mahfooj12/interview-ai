"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import type { ReportSections } from "@/types/report";

const ROWS: { key: keyof ReportSections; label: string }[] = [
  { key: "technical", label: "Technical" },
  { key: "communication", label: "Communication" },
  { key: "problemSolving", label: "Problem solving" },
  { key: "confidence", label: "Confidence" },
  { key: "resumeKnowledge", label: "Resume knowledge" },
  { key: "projectKnowledge", label: "Project knowledge" },
];

export function SectionBreakdown({ sections }: { sections: ReportSections }) {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Score breakdown</CardTitle>
      </CardHeader>
      <CardContent className="space-y-3">
        {ROWS.map((row) => {
          const value = sections[row.key] ?? 0;
          return (
            <div key={row.key} className="space-y-1">
              <div className="flex items-center justify-between text-sm">
                <span className="text-muted-foreground">{row.label}</span>
                <span className="font-medium">{Math.round(value)}</span>
              </div>
              <Progress value={value} />
            </div>
          );
        })}
      </CardContent>
    </Card>
  );
}