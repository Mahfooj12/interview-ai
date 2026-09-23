"use client";

import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import type { MatchResult } from "@/types/job";

interface JobMatchResultProps {
  match: MatchResult;
}

export function JobMatchResult({ match }: JobMatchResultProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Resume–Job match</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        <div>
          <div className="mb-1 flex items-center justify-between text-sm">
            <span>Match percentage</span>
            <span className="font-medium">
              {Math.round(match.matchPercentage)}%
            </span>
          </div>
          <Progress value={match.matchPercentage} />
        </div>

        <MatchGroup
          label="Matched skills"
          items={match.matchedSkills}
          variant="success"
        />
        <MatchGroup
          label="Partially matched"
          items={match.partiallyMatchedSkills}
          variant="warning"
        />
        <MatchGroup
          label="Missing skills"
          items={match.missingSkills}
          variant="destructive"
        />
      </CardContent>
    </Card>
  );
}

function MatchGroup({
  label,
  items,
  variant,
}: {
  label: string;
  items: string[];
  variant: "success" | "warning" | "destructive";
}) {
  if (!items || items.length === 0) return null;
  return (
    <div>
      <p className="mb-1 text-xs font-medium uppercase tracking-wide text-muted-foreground">
        {label}
      </p>
      <div className="flex flex-wrap gap-1">
        {items.map((item) => (
          <Badge key={item} variant={variant}>
            {item}
          </Badge>
        ))}
      </div>
    </div>
  );
}