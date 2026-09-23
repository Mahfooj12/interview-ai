"use client";

import { useCallback, useEffect, useRef, useState } from "react";

interface UseAutoListenOptions {
  /** How long the user must be silent (ms) before we consider them done. */
  silenceMs?: number;
  /** Minimum speech duration (ms) before we accept the answer. */
  minSpeechMs?: number;
  /** Max time to listen (ms) — auto-submits if exceeded. */
  maxListenMs?: number;
  /** Called when we detect the user has finished speaking. */
  onDone: (reason: "silence" | "maxTime" | "manual") => void;
  /** Called on each tick with total elapsed ms. */
  onTick?: (elapsedMs: number) => void;
}

/**
 * Auto-listen hook.
 *
 * Watches for the user to start speaking, then tracks pauses. When the user
 * has been silent for `silenceMs`, fires `onDone("silence")`.
 *
 * Exposes `silenceMs` (current silence duration) for UI countdowns.
 */
export function useAutoListen(options: UseAutoListenOptions) {
  const {
    silenceMs = 15000,
    minSpeechMs = 1500,
    maxListenMs = 180_000,
    onDone,
    onTick,
  } = options;

  const [isActive, setIsActive] = useState(false);
  const [hasSpoken, setHasSpoken] = useState(false);
  const [elapsedMs, setElapsedMs] = useState(0);
  const [currentSilenceMs, setCurrentSilenceMs] = useState(0);

  const speechStartRef = useRef<number | null>(null);
  const lastSpeechAtRef = useRef<number>(0);
  const listenStartRef = useRef<number>(0);
  const tickerRef = useRef<number | null>(null);
  const onDoneRef = useRef(onDone);
  onDoneRef.current = onDone;

  const stop = useCallback(() => {
    if (tickerRef.current !== null) {
      window.clearInterval(tickerRef.current);
      tickerRef.current = null;
    }
    speechStartRef.current = null;
    lastSpeechAtRef.current = 0;
    listenStartRef.current = 0;
    setElapsedMs(0);
    setCurrentSilenceMs(0);
    setHasSpoken(false);
    setIsActive(false);
  }, []);

  /** Called by the speech recognizer whenever interim/final text arrives. */
  const pingSpeech = useCallback(() => {
    const now = Date.now();
    if (speechStartRef.current === null) {
      speechStartRef.current = now;
    }
    lastSpeechAtRef.current = now;
    setHasSpoken(true);
  }, []);

  /** Called by the recognizer on speechend / soundend. */
  const pingSilence = useCallback(() => {
    if (lastSpeechAtRef.current === 0) {
      lastSpeechAtRef.current = Date.now();
    }
  }, []);

  const start = useCallback(() => {
    console.log("[AutoListen] start");
    speechStartRef.current = null;
    lastSpeechAtRef.current = 0;
    listenStartRef.current = Date.now();
    setElapsedMs(0);
    setCurrentSilenceMs(0);
    setHasSpoken(false);
    setIsActive(true);
  }, []);

  // Ticker — checks silence each interval
  useEffect(() => {
    if (!isActive) return;

    tickerRef.current = window.setInterval(() => {
      const now = Date.now();
      const totalElapsed = now - listenStartRef.current;
      setElapsedMs(totalElapsed);
      onTick?.(totalElapsed);

      // Max-time exceeded
      if (totalElapsed >= maxListenMs) {
        console.log("[AutoListen] max time reached");
        onDoneRef.current("maxTime");
        stop();
        return;
      }

      // No speech yet — keep waiting (subject to max time)
      if (speechStartRef.current === null) return;

      const speechDuration = now - (speechStartRef.current ?? now);
      const silence = now - lastSpeechAtRef.current;
      setCurrentSilenceMs(silence);

      // Wait for minimum speech before allowing auto-stop
      if (speechDuration < minSpeechMs) return;

      // User has been silent long enough
      if (silence >= silenceMs) {
        console.log(
          `[AutoListen] silence detected (${silence}ms) — done`
        );
        onDoneRef.current("silence");
        stop();
      }
    }, 250);

    return () => {
      if (tickerRef.current !== null) {
        window.clearInterval(tickerRef.current);
        tickerRef.current = null;
      }
    };
  }, [isActive, silenceMs, minSpeechMs, maxListenMs, onTick, stop]);

  return {
    isActive,
    hasSpoken,
    elapsedMs,
    silenceMs: currentSilenceMs,
    start,
    stop,
    pingSpeech,
    pingSilence,
  };
}