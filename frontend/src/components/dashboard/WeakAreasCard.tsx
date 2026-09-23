"use client";

import Link from "next/link";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

interface WeakAreasCardProps {
  areas: string[];
}

export function WeakAreasCard({ areas }: WeakAreasCardProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Weak areas</CardTitle>
      </CardHeader>
      <CardContent className="space-y-3">
        {areas.length === 0 ? (
          <p className="text-sm text-muted-foreground">
            No weak areas identified yet.
          </p>
        ) : (
          <>
            <div className="flex flex-wrap gap-2">
              {areas.slice(0, 8).map((area) => (
                <Badge key={area} variant="warning">
                  {area}
                </Badge>
              ))}
            </div>
            <Button asChild size="sm" variant="outline" className="w-full">
              <Link href={`/interview/setup?focus=${encodeURIComponent(areas[0])}`}>
                Practice weak areas
              </Link>
            </Button>
          </>
        )}
      </CardContent>
    </Card>
  );
}