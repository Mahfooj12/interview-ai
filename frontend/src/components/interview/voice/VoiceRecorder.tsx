"use client";

import { Mic, Square, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { VoiceWaveform } from "./VoiceWaveform";
import type { RecorderState } from "@/types/voice";

interface VoiceRecorderProps {
  state: RecorderState;
  level: number;
  onStart: () => void;
  onStop: () => void;
  onCancel: () => void;
  disabled?: boolean;
}

export function VoiceRecorder({
  state,
  level,
  onStart,
  onStop,
  onCancel,
  disabled,
}: VoiceRecorderProps) {
  const recording = state === "recording";

  return (
    <div className="flex flex-col items-center gap-3 rounded-lg border bg-card p-4">
      <VoiceWaveform level={level} active={recording} />

      <div className="flex items-center gap-2">
        {!recording ? (
          <Button
            onClick={onStart}
            disabled={disabled}
            className="rounded-full"
          >
            <Mic className="mr-2 h-4 w-4" />
            Start recording
          </Button>
        ) : (
          <>
            <Button
              onClick={onStop}
              variant="default"
              className="rounded-full"
            >
              <Square className="mr-2 h-4 w-4" />
              Stop &amp; send
            </Button>
            <Button
              onClick={onCancel}
              variant="ghost"
              className="rounded-full"
            >
              <Trash2 className="h-4 w-4" />
            </Button>
          </>
        )}
      </div>

      <p className="text-xs text-muted-foreground">
        {recording
          ? "Listening… speak clearly. Auto-stops after silence."
          : "Tap start to answer with your voice."}
      </p>
    </div>
  );
}