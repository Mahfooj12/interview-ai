"use client";

import { cn } from "@/lib/utils";

interface VoiceWaveformProps {
  level: number; // 0..1
  active: boolean;
  className?: string;
}

export function VoiceWaveform({ level, active, className }: VoiceWaveformProps) {
  const bars = 24;
  return (
    <div className={cn("flex h-10 items-center gap-0.5", className)}>
      {Array.from({ length: bars }).map((_, i) => {
        const distance = Math.abs(i - bars / 2) / (bars / 2);
        const intensity = active ? Math.max(0.08, level - distance * 0.4) : 0.08;
        const height = 6 + intensity * 34;
        return (
          <span
            key={i}
            className="w-1 rounded-full bg-primary transition-all"
            style={{ height: `${height}px` }}
          />
        );
      })}
    </div>
  );
}