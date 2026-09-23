"use client";

import Link from "next/link";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

interface WeakAreasPanelProps {
  weakAreas: string[];
  strongAreas: string[];
  cumulativeScore: number;
}

export function WeakAreasPanel({
  weakAreas,
  strongAreas,
  cumulativeScore,
}: WeakAreasPanelProps) {
  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between space-y-0">
        <CardTitle className="text-base">Session state</CardTitle>
        <span className="text-sm text-muted-foreground">
          Avg: {Math.round(cumulativeScore)}
        </span>
      </CardHeader>
      <CardContent className="space-y-4">
        <div>
          <p className="mb-1 text-xs font-medium uppercase tracking-wide text-muted-foreground">
            Strong so far
          </p>
          {strongAreas.length === 0 ? (
            <p className="text-sm text-muted-foreground">None yet.</p>
          ) : (
            <div className="flex flex-wrap gap-1">
              {strongAreas.map((a) => (
                <Badge key={a} variant="success">
                  {a}
                </Badge>
              ))}
            </div>
          )}
        </div>

        <div>
          <p className="mb-1 text-xs font-medium uppercase tracking-wide text-muted-foreground">
            Weak so far
          </p>
          {weakAreas.length === 0 ? (
            <p className="text-sm text-muted-foreground">None yet.</p>
          ) : (
            <div className="flex flex-wrap gap-1">
              {weakAreas.map((a) => (
                <Badge key={a} variant="warning">
                  {a}
                </Badge>
              ))}
            </div>
          )}
        </div>

        {weakAreas.length > 0 ? (
          <Button asChild size="sm" variant="outline" className="w-full">
            <Link
              href={`/interview/setup?focus=${encodeURIComponent(weakAreas[0])}`}
            >
              Practice weak areas
            </Link>
          </Button>
        ) : null}
      </CardContent>
    </Card>
  );
}