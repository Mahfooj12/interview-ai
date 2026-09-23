"use client";

import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

interface StrongAreasCardProps {
  areas: string[];
}

export function StrongAreasCard({ areas }: StrongAreasCardProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Strong areas</CardTitle>
      </CardHeader>
      <CardContent>
        {areas.length === 0 ? (
          <p className="text-sm text-muted-foreground">
            Complete interviews to identify strengths.
          </p>
        ) : (
          <div className="flex flex-wrap gap-2">
            {areas.slice(0, 8).map((area) => (
              <Badge key={area} variant="success">
                {area}
              </Badge>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  );
}