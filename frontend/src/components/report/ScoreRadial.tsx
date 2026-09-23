"use client";

import {
  PolarAngleAxis,
  RadialBar,
  RadialBarChart,
  ResponsiveContainer,
} from "recharts";

interface ScoreRadialProps {
  score: number;
  size?: number;
  label?: string;
}

export function ScoreRadial({ score, size = 160, label = "Overall" }: ScoreRadialProps) {
  const data = [{ name: label, value: Math.max(0, Math.min(100, score)), fill: "hsl(var(--primary))" }];

  return (
    <div className="relative" style={{ width: size, height: size }}>
      <ResponsiveContainer width="100%" height="100%">
        <RadialBarChart
          innerRadius="72%"
          outerRadius="100%"
          data={data}
          startAngle={90}
          endAngle={-270}
        >
          <PolarAngleAxis type="number" domain={[0, 100]} tick={false} />
          <RadialBar background dataKey="value" cornerRadius={20} />
        </RadialBarChart>
      </ResponsiveContainer>
      <div className="pointer-events-none absolute inset-0 flex flex-col items-center justify-center">
        <span className="text-3xl font-bold">{Math.round(score)}</span>
        <span className="text-xs text-muted-foreground">{label}</span>
      </div>
    </div>
  );
}