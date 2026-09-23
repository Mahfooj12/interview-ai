"use client";

import { useEffect, useRef, useState } from "react";
import { Camera, CameraOff } from "lucide-react";
import { Button } from "@/components/ui/button";

interface CameraPreviewProps {
  enabled: boolean;
}

export function CameraPreview({ enabled }: CameraPreviewProps) {
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const streamRef = useRef<MediaStream | null>(null);
  const [active, setActive] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!enabled) {
      stopCamera();
      return;
    }
    startCamera();
    return () => stopCamera();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [enabled]);

  const startCamera = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { width: 320, height: 240, facingMode: "user" },
        audio: false, // mic is handled by SpeechRecognition
      });
      streamRef.current = stream;
      if (videoRef.current) {
        videoRef.current.srcObject = stream;
        await videoRef.current.play().catch(() => {});
      }
      setActive(true);
      setError(null);
    } catch (err: any) {
      console.warn("[Camera] failed:", err);
      setError("Camera unavailable");
      setActive(false);
    }
  };

  const stopCamera = () => {
    streamRef.current?.getTracks().forEach((t) => t.stop());
    streamRef.current = null;
    if (videoRef.current) videoRef.current.srcObject = null;
    setActive(false);
  };

  return (
    <div className="relative aspect-video w-full overflow-hidden rounded-lg border bg-slate-900">
      <video
        ref={videoRef}
        className="h-full w-full object-cover"
        muted
        playsInline
        autoPlay
      />
      {!active ? (
        <div className="absolute inset-0 flex flex-col items-center justify-center gap-2 text-white/50">
          <CameraOff className="h-8 w-8" />
          <p className="text-xs">{error ?? "Camera off"}</p>
        </div>
      ) : null}
      <div className="absolute bottom-2 left-2 flex items-center gap-1.5 rounded-full bg-black/60 px-2 py-0.5 backdrop-blur">
        <Camera className="h-3 w-3 text-white/80" />
        <span className="text-[10px] font-medium text-white">You</span>
      </div>
    </div>
  );
}