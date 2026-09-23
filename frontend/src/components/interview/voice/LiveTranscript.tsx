"use client";

import { useEffect, useRef } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import type { TranscriptSegment } from "@/types/voice";

interface LiveTranscriptProps {
  segments: TranscriptSegment[];
  finalText: string;
}

export function LiveTranscript({ segments, finalText }: LiveTranscriptProps) {
  const containerRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (containerRef.current) {
      containerRef.current.scrollTop = containerRef.current.scrollHeight;
    }
  }, [segments]);

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Live transcript</CardTitle>
      </CardHeader>
      <CardContent>
        <div
          ref={containerRef}
          className="max-h-56 min-h-24 overflow-y-auto rounded-md bg-muted p-3 text-sm"
        >
          {segments.length === 0 ? (
            <p className="text-muted-foreground">
              Your spoken answer will appear here.
            </p>
          ) : (
            <p className="whitespace-pre-wrap leading-relaxed">{finalText}</p>
          )}
        </div>
      </CardContent>
    </Card>
  );
}