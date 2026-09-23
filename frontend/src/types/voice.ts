export type RecorderState = "idle" | "recording" | "paused" | "stopped";

export interface TranscriptSegment {
  id: string;
  text: string;
  final: boolean;
  createdAt: number;
}

export interface SttResponse {
  transcript: string;
  durationMs: number;
  confidence: number | null;
}

export interface TtsResponse {
  audioUrl: string;
  durationMs: number;
}

export interface AudioAnswerPayload {
  questionId: string;
  transcript: string;
  audioUrl?: string;
  durationMs?: number;
}