"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";

interface StrengthsWeaknessesProps {
  strongAreas: string[];
  weakAreas: string[];
}

export function StrengthsWeaknesses({
  strongAreas,
  weakAreas,
}: StrengthsWeaknessesProps) {
  return (
    <div className="grid gap-4 md:grid-cols-2">
      <Card>
        <CardHeader>
          <CardTitle className="text-base">Strengths</CardTitle>
        </CardHeader>
        <CardContent>
          {strongAreas.length === 0 ? (
            <p className="text-sm text-muted-foreground">None detected.</p>
          ) : (
            <div className="flex flex-wrap gap-2">
              {strongAreas.map((area) => (
                <Badge key={area} variant="success">
                  {area}
                </Badge>
              ))}
            </div>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Weaknesses</CardTitle>
        </CardHeader>
        <CardContent>
          {weakAreas.length === 0 ? (
            <p className="text-sm text-muted-foreground">None detected.</p>
          ) : (
            <div className="flex flex-wrap gap-2">
              {weakAreas.map((area) => (
                <Badge key={area} variant="warning">
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