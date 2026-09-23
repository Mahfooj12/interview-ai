"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import type { TtsResponse } from "@/types/voice";

/**
 * Plays TTS audio returned by the backend (URL or base64 data URL).
 * Falls back to the browser SpeechSynthesis API if the backend does not
 * provide audio.
 */
export function useSpeechSynthesis() {
  const [speaking, setSpeaking] = useState(false);
  const audioRef = useRef<HTMLAudioElement | null>(null);

  const stop = useCallback(() => {
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current.currentTime = 0;
      audioRef.current = null;
    }
    if (typeof window !== "undefined" && "speechSynthesis" in window) {
      window.speechSynthesis.cancel();
    }
    setSpeaking(false);
  }, []);

  const speakUrl = useCallback(
    (audioUrl: string) =>
      new Promise<void>((resolve, reject) => {
        stop();
        const audio = new Audio(audioUrl);
        audioRef.current = audio;
        audio.onplay = () => setSpeaking(true);
        audio.onended = () => {
          setSpeaking(false);
          audioRef.current = null;
          resolve();
        };
        audio.onerror = (e) => {
          setSpeaking(false);
          audioRef.current = null;
          reject(e);
        };
        audio.play().catch(reject);
      }),
    [stop],
  );

  const speakBrowser = useCallback(
    (text: string) =>
      new Promise<void>((resolve) => {
        if (typeof window === "undefined" || !("speechSynthesis" in window)) {
          resolve();
          return;
        }
        stop();
        const utter = new SpeechSynthesisUtterance(text);
        utter.rate = 1;
        utter.pitch = 1;
        utter.onstart = () => setSpeaking(true);
        utter.onend = () => {
          setSpeaking(false);
          resolve();
        };
        utter.onerror = () => {
          setSpeaking(false);
          resolve();
        };
        window.speechSynthesis.speak(utter);
      }),
    [stop],
  );

  const speakResponse = useCallback(
    async (tts: TtsResponse | null, fallbackText: string) => {
      if (tts?.audioUrl) {
        try {
          await speakUrl(tts.audioUrl);
          return;
        } catch {
          // fall through to browser synthesis
        }
      }
      await speakBrowser(fallbackText);
    },
    [speakBrowser, speakUrl],
  );

  useEffect(() => () => stop(), [stop]);

  return { speaking, speakResponse, speakBrowser, stop };
}

