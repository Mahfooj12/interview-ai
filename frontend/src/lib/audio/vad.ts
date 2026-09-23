"use client";

/**
 * Naive voice activity detection based on RMS threshold.
 * Returns a subscribe function that fires when speech begins/ends.
 */
export interface VadOptions {
  threshold?: number;     // 0..1
  silenceMs?: number;     // duration of silence before onEnd
  minSpeechMs?: number;   // minimum speech duration before onEnd
  onSpeechStart?: () => void;
  onSpeechEnd?: () => void;
}

export function createVad(options: VadOptions = {}) {
  const threshold = options.threshold ?? 0.06;
  const silenceMs = options.silenceMs ?? 1400;
  const minSpeechMs = options.minSpeechMs ?? 400;

  let speaking = false;
  let speechStartedAt = 0;
  let lastLoudAt = 0;

  return {
    push(level: number) {
      const now = performance.now();
      if (level > threshold) {
        lastLoudAt = now;
        if (!speaking) {
          speaking = true;
          speechStartedAt = now;
          options.onSpeechStart?.();
        }
      } else if (speaking) {
        const silence = now - lastLoudAt;
        const speech = now - speechStartedAt;
        if (silence >= silenceMs && speech >= minSpeechMs) {
          speaking = false;
          options.onSpeechEnd?.();
        }
      }
    },
    reset() {
      speaking = false;
      speechStartedAt = 0;
      lastLoudAt = 0;
    },
  };
}