"use client";

import Link from "next/link";
import { ExternalLink } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import type { LearningItem } from "@/types/report";

interface LearningRoadmapProps {
  items: LearningItem[];
  weakAreas: string[];
}

const priorityVariant: Record<
  LearningItem["priority"],
  "destructive" | "warning" | "secondary"
> = {
  HIGH: "destructive",
  MEDIUM: "warning",
  LOW: "secondary",
};

export function LearningRoadmap({ items, weakAreas }: LearningRoadmapProps) {
  const focus = weakAreas[0];

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between space-y-0">
        <CardTitle className="text-base">Learning roadmap</CardTitle>
        {focus ? (
          <Button asChild size="sm" variant="outline">
            <Link
              href={`/interview/setup?focus=${encodeURIComponent(focus)}`}
            >
              Practice {focus}
            </Link>
          </Button>
        ) : null}
      </CardHeader>
      <CardContent className="space-y-5">
        {items.length === 0 ? (
          <p className="text-sm text-muted-foreground">
            No recommendations yet.
          </p>
        ) : (
          items.map((item, idx) => (
            <div key={item.topic} className="space-y-2">
              <div className="flex items-start justify-between gap-2">
                <p className="font-medium">{item.topic}</p>
                <Badge variant={priorityVariant[item.priority]}>
                  {item.priority}
                </Badge>
              </div>

              {item.resources.length > 0 ? (
                <ul className="space-y-1 text-sm">
                  {item.resources.map((res) => (
                    <li key={`${res.type}-${res.url}`}>
                      <a
                        href={res.url}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="inline-flex items-center gap-1 text-primary hover:underline"
                      >
                        <span className="text-xs text-muted-foreground">
                          [{res.type}]
                        </span>
                        {res.title}
                        <ExternalLink className="h-3 w-3" />
                      </a>
                    </li>
                  ))}
                </ul>
              ) : null}

              {idx < items.length - 1 ? <Separator className="mt-3" /> : null}
            </div>
          ))
        )}
      </CardContent>
    </Card>
  );
}