"use client";

import { useCallback, useEffect, useRef, useState } from "react";

interface UseSpeechRecognitionOptions {
  onResult?: (transcript: string) => void;
  onEnd?: () => void;
  /** Called whenever speech is detected — used by auto-listen to reset silence. */
  onSpeech?: () => void;
  lang?: string;
}

export function useSpeechRecognition(options: UseSpeechRecognitionOptions = {}) {
  const [isListening, setIsListening] = useState(false);
  const [transcript, setTranscript] = useState("");
  const [interimTranscript, setInterimTranscript] = useState("");
  const [supported, setSupported] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const recognitionRef = useRef<any>(null);
  const manualStopRef = useRef(false);
  const transcriptRef = useRef("");
  const optionsRef = useRef(options);
  optionsRef.current = options;

  const endResolversRef = useRef<Array<() => void>>([]);

  useEffect(() => {
    const SpeechRecognition =
      (window as any).SpeechRecognition ||
      (window as any).webkitSpeechRecognition;

    if (!SpeechRecognition) {
      console.warn("[SR] SpeechRecognition not supported");
      setSupported(false);
      return;
    }

    console.log("[SR] Creating SpeechRecognition instance");
    setSupported(true);

    const recognition = new SpeechRecognition();
    recognition.continuous = true;
    recognition.interimResults = true;
    recognition.lang = "en-US";
    recognition.maxAlternatives = 1;

    recognition.onstart = () => {
      console.log("[SR] ✅ onstart — Chrome is LIVE");
    };
    recognition.onaudiostart = () => {
      console.log("[SR] ✅ onaudiostart — mic stream received");
    };
    recognition.onsoundstart = () => {
      console.log("[SR] ✅ onsoundstart — sound detected");
      optionsRef.current.onSpeech?.();
    };
    recognition.onspeechstart = () => {
      console.log("[SR] ✅ onspeechstart — speech detected");
      optionsRef.current.onSpeech?.();
    };
    recognition.onspeechend = () => {
      console.log("[SR] onspeechend");
      // Also fire onSpeech so the last speech event updates the timer.
      optionsRef.current.onSpeech?.();
    };
    recognition.onsoundend = () => {
      console.log("[SR] onsoundend");
    };
    recognition.onaudioend = () => {
      console.log("[SR] onaudioend");
    };

    recognition.onresult = (event: any) => {
      let finalText = "";
      let interimText = "";
      for (let i = event.resultIndex; i < event.results.length; i++) {
        const result = event.results[i];
        const text = result[0].transcript;
        if (result.isFinal) {
          finalText += text + " ";
        } else {
          interimText += text;
        }
      }

      // ⚡ Notify auto-listen on every result update.
      optionsRef.current.onSpeech?.();

      if (finalText) {
        transcriptRef.current = (transcriptRef.current + " " + finalText).trim();
        console.log("[SR] FINAL:", transcriptRef.current);
        setTranscript(transcriptRef.current);
        optionsRef.current.onResult?.(transcriptRef.current);
      }
      if (interimText) {
        console.log("[SR] interim:", interimText);
      }
      setInterimTranscript(interimText);
    };

    recognition.onnomatch = () => {
      console.log("[SR] onnomatch");
    };

    recognition.onerror = (event: any) => {
      console.warn("[SR] onerror:", event.error);
      if (event.error === "no-speech" || event.error === "aborted") {
        return;
      }
      setError(event.error);
    };

    recognition.onend = () => {
      console.log("[SR] onend");
      setIsListening(false);

      const resolvers = endResolversRef.current;
      endResolversRef.current = [];
      resolvers.forEach((fn) => fn());

      if (!manualStopRef.current && recognitionRef.current) {
        console.log("[SR] auto-restart scheduled");
        setTimeout(() => {
          if (manualStopRef.current) return;
          try {
            recognitionRef.current.start();
            console.log("[SR] auto-restart called");
          } catch (e: any) {
            if (e.name !== "InvalidStateError") {
              console.warn("[SR] auto-restart failed:", e);
            }
          }
        }, 400);
      }
    };

    recognitionRef.current = recognition;

    return () => {
      console.log("[SR] cleanup");
      manualStopRef.current = true;
      try {
        recognition.stop();
      } catch {}
      recognitionRef.current = null;
    };
  }, []);

  const startListening = useCallback(() => {
    if (!recognitionRef.current) {
      console.warn("[SR] startListening: no recognition instance");
      return;
    }
    console.log("[SR] startListening called");
    manualStopRef.current = false;
    transcriptRef.current = "";
    setTranscript("");
    setInterimTranscript("");
    setError(null);
    try {
      recognitionRef.current.start();
      console.log("[SR] .start() returned");
      setIsListening(true);
    } catch (e: any) {
      if (e.name === "InvalidStateError") {
        console.log("[SR] already running, stopping first");
        try {
          recognitionRef.current.stop();
          setTimeout(() => {
            try {
              recognitionRef.current.start();
            } catch {}
          }, 200);
        } catch {}
      } else {
        console.warn("[SR] .start() threw:", e);
      }
    }
  }, []);

  const stopListening = useCallback((): Promise<void> => {
    console.log("[SR] stopListening called");
    manualStopRef.current = true;

    return new Promise<void>((resolve) => {
      if (!recognitionRef.current) {
        resolve();
        return;
      }
      endResolversRef.current.push(resolve);
      try {
        recognitionRef.current.stop();
      } catch {
        resolve();
      }
      setIsListening(false);

      setTimeout(() => {
        const resolvers = endResolversRef.current;
        endResolversRef.current = [];
        resolvers.forEach((fn) => fn());
      }, 3000);
    });
  }, []);

  const resetTranscript = useCallback(() => {
    transcriptRef.current = "";
    setTranscript("");
    setInterimTranscript("");
  }, []);

  return {
    isListening,
    transcript,
    interimTranscript,
    startListening,
    stopListening,
    resetTranscript,
    supported,
    error,
    getTranscript: () => transcriptRef.current,
  };
}