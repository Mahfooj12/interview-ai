"use client";

import {
  PolarAngleAxis,
  PolarGrid,
  Radar,
  RadarChart,
  ResponsiveContainer,
} from "recharts";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import type { ProgressSkill } from "@/types/report";

export function SkillRadar({ skills }: { skills: ProgressSkill[] }) {
  const data = skills.slice(0, 6).map((s) => ({
    skill: s.skill,
    score: Math.round(s.averageScore),
  }));

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Skill profile</CardTitle>
      </CardHeader>
      <CardContent className="h-72">
        {data.length === 0 ? (
          <div className="flex h-full items-center justify-center text-sm text-muted-foreground">
            Complete interviews to build your skill profile.
          </div>
        ) : (
          <ResponsiveContainer width="100%" height="100%">
            <RadarChart data={data}>
              <PolarGrid />
              <PolarAngleAxis dataKey="skill" className="text-xs" />
              <Radar
                dataKey="score"
                stroke="hsl(var(--primary))"
                fill="hsl(var(--primary))"
                fillOpacity={0.35}
              />
            </RadarChart>
          </ResponsiveContainer>
        )}
      </CardContent>
    </Card>
  );
}