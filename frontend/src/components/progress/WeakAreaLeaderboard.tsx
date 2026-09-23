"use client";

import Link from "next/link";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

interface WeakAreaLeaderboardProps {
  weakAreas: string[];
  strongAreas: string[];
}

export function WeakAreaLeaderboard({
  weakAreas,
  strongAreas,
}: WeakAreaLeaderboardProps) {
  return (
    <div className="grid gap-4 md:grid-cols-2">
      <Card>
        <CardHeader>
          <CardTitle className="text-base">Top weak areas</CardTitle>
        </CardHeader>
        <CardContent className="space-y-3">
          {weakAreas.length === 0 ? (
            <p className="text-sm text-muted-foreground">None yet.</p>
          ) : (
            <>
              <div className="flex flex-wrap gap-2">
                {weakAreas.slice(0, 8).map((area) => (
                  <Badge key={area} variant="warning">
                    {area}
                  </Badge>
                ))}
              </div>
              <Button asChild size="sm" variant="outline" className="w-full">
                <Link
                  href={`/interview/setup?focus=${encodeURIComponent(weakAreas[0])}`}
                >
                  Practice {weakAreas[0]}
                </Link>
              </Button>
            </>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Top strong areas</CardTitle>
        </CardHeader>
        <CardContent>
          {strongAreas.length === 0 ? (
            <p className="text-sm text-muted-foreground">None yet.</p>
          ) : (
            <div className="flex flex-wrap gap-2">
              {strongAreas.slice(0, 8).map((area) => (
                <Badge key={area} variant="success">
                  {area}
                </Badge>
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}