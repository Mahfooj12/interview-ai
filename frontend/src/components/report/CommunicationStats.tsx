"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import type { CommunicationStats as Stats } from "@/types/report";

interface CommunicationStatsProps {
  stats: Stats | null;
}

export function CommunicationStats({ stats }: CommunicationStatsProps) {
  if (!stats) return null;
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Communication analysis</CardTitle>
      </CardHeader>
      <CardContent className="grid gap-3 sm:grid-cols-2">
        <Stat label="Words per minute" value={Math.round(stats.wpm)} />
        <Stat label="Filler words" value={stats.fillerWordCount} />
        <Stat label="Hesitations" value={stats.hesitationCount} />
        <Stat label="Clarity" value={Math.round(stats.clarityScore)} />
        <Stat label="Confidence" value={Math.round(stats.confidenceScore)} />
        <Stat label="Grammar" value={Math.round(stats.grammarScore)} />

        {stats.fillerWords && stats.fillerWords.length > 0 ? (
          <div className="sm:col-span-2">
            <p className="mb-1 text-xs font-medium uppercase tracking-wide text-muted-foreground">
              Detected fillers
            </p>
            <div className="flex flex-wrap gap-1">
              {stats.fillerWords.map((w) => (
                <Badge key={w} variant="warning">
                  {w}
                </Badge>
              ))}
            </div>
          </div>
        ) : null}
      </CardContent>
    </Card>
  );
}

function Stat({ label, value }: { label: string; value: number | string }) {
  return (
    <div className="rounded-md border p-3">
      <p className="text-xs text-muted-foreground">{label}</p>
      <p className="mt-1 text-lg font-semibold">{value}</p>
    </div>
  );
}