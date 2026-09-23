"use client";

import { useEffect, useRef } from "react";
import { Button } from "@/components/ui/button";
import { Play, Pause } from "lucide-react";
import { useState } from "react";

export function AudioPlayer({ src }: { src: string }) {
  const audioRef = useRef<HTMLAudioElement | null>(null);
  const [playing, setPlaying] = useState(false);

  useEffect(() => {
    setPlaying(false);
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current.currentTime = 0;
    }
  }, [src]);

  const toggle = () => {
    const audio = audioRef.current;
    if (!audio) return;
    if (audio.paused) {
      audio.play().catch(() => setPlaying(false));
    } else {
      audio.pause();
    }
  };

  return (
    <div className="flex items-center gap-2">
      <audio
        ref={audioRef}
        src={src}
        onPlay={() => setPlaying(true)}
        onPause={() => setPlaying(false)}
        onEnded={() => setPlaying(false)}
        hidden
      />
      <Button size="sm" variant="outline" onClick={toggle}>
        {playing ? (
          <Pause className="mr-1 h-4 w-4" />
        ) : (
          <Play className="mr-1 h-4 w-4" />
        )}
        {playing ? "Pause" : "Play"}
      </Button>
    </div>
  );
}