"use client";

import { useCallback, useRef, useState } from "react";
import { AudioRecorder } from "@/lib/audio/recorder";
import { createVad } from "@/lib/audio/vad";
import type { RecorderState } from "@/types/voice";

interface UseVoiceRecorderOptions {
  silenceMs?: number;
  threshold?: number;
  onAutoStop?: (blob: Blob) => void;
}

export function useVoiceRecorder(options: UseVoiceRecorderOptions = {}) {
  const [state, setState] = useState<RecorderState>("idle");
  const [level, setLevel] = useState(0);
  const [lastBlob, setLastBlob] = useState<Blob | null>(null);

  const recorderRef = useRef<AudioRecorder | null>(null);
  const vadRef = useRef(
    createVad({
      silenceMs: options.silenceMs ?? 1400,
      threshold: options.threshold ?? 0.06,
      onSpeechEnd: () => {
        // auto-stop and emit
        if (!recorderRef.current) return;
        recorderRef.current.stop().then((blob) => {
          if (blob && options.onAutoStop) options.onAutoStop(blob);
        });
        recorderRef.current = null;
        setState("stopped");
      },
    }),
  );

    const start = useCallback(async () => {
    console.log("[Recorder] start() called");
    try {
      const recorder = new AudioRecorder({
        onLevel: (l) => {
          setLevel(l);
          vadRef.current.push(l);
        },
        onData: (blob) => {
          setLastBlob(blob);
        },
      });

      console.log("[Recorder] requesting getUserMedia...");
      await recorder.start();
      console.log("[Recorder] getUserMedia OK, state = recording");

      recorderRef.current = recorder;
      setState("recording");
    } catch (err) {
      console.error("[Recorder] failed to start:", err);
      setState("idle");
    }
  }, []);

  const stop = useCallback(async () => {
    if (!recorderRef.current) {
      setState("idle");
      return null;
    }
    const blob = await recorderRef.current.stop();
    recorderRef.current = null;
    setState("stopped");
    return blob;
  }, []);

  const cancel = useCallback(() => {
    recorderRef.current?.cancel();
    recorderRef.current = null;
    setState("idle");
  }, []);

  return {
    state,
    level,
    lastBlob,
    start,
    stop,
    cancel,
  };
}