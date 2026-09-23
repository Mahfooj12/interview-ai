"use client";

import type { TranscriptSegment } from "@/types/voice";

let counter = 0;

export function createSegment(text: string, final = false): TranscriptSegment {
  counter += 1;
  return {
    id: `seg_${Date.now()}_${counter}`,
    text,
    final,
    createdAt: Date.now(),
  };
}

export function mergeTranscript(segments: TranscriptSegment[]): string {
  return segments.map((s) => s.text).join(" ").replace(/\s+/g, " ").trim();
}