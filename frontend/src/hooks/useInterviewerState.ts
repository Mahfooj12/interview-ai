"use client";

import { useCallback, useMemo, useState } from "react";

/**
 * Interviewer states — controls the visual + behavioral mode of the AI.
 *
 * IDLE       — waiting for the interview to start
 * LISTENING  — candidate is speaking; interviewer listens
 * THINKING   — interviewer is processing the answer (short pause)
 * SPEAKING   — interviewer is reading the next question
 * WAITING    — interviewer has finished asking; giving candidate time to start
 * ENDING     — interview is being completed
 */
export type InterviewerState =
  | "IDLE"
  | "LISTENING"
  | "THINKING"
  | "SPEAKING"
  | "WAITING"
  | "ENDING";

interface UseInterviewerStateResult {
  state: InterviewerState;
  isIdle: boolean;
  isListening: boolean;
  isThinking: boolean;
  isSpeaking: boolean;
  isWaiting: boolean;
  isEnding: boolean;
  setState: (s: InterviewerState) => void;
  reset: () => void;
  /** Human-readable label for UI badges */
  label: string;
  /** Tailwind color class for the state dot */
  color: string;
}

export function useInterviewerState(
  initial: InterviewerState = "IDLE"
): UseInterviewerStateResult {
  const [state, setStateRaw] = useState<InterviewerState>(initial);

  const setState = useCallback((s: InterviewerState) => {
    console.log(`[Interviewer] state → ${s}`);
    setStateRaw(s);
  }, []);

  const reset = useCallback(() => setState("IDLE"), [setState]);

  const label = useMemo(() => {
    switch (state) {
      case "IDLE":
        return "Ready";
      case "LISTENING":
        return "Listening…";
      case "THINKING":
        return "Thinking…";
      case "SPEAKING":
        return "Speaking…";
      case "WAITING":
        return "Waiting for you";
      case "ENDING":
        return "Wrapping up…";
    }
  }, [state]);

  const color = useMemo(() => {
    switch (state) {
      case "IDLE":
        return "bg-slate-400";
      case "LISTENING":
        return "bg-emerald-500";
      case "THINKING":
        return "bg-amber-500";
      case "SPEAKING":
        return "bg-blue-500";
      case "WAITING":
        return "bg-sky-400";
      case "ENDING":
        return "bg-rose-500";
    }
  }, [state]);

  return {
    state,
    isIdle: state === "IDLE",
    isListening: state === "LISTENING",
    isThinking: state === "THINKING",
    isSpeaking: state === "SPEAKING",
    isWaiting: state === "WAITING",
    isEnding: state === "ENDING",
    setState,
    reset,
    label,
    color,
  };
}