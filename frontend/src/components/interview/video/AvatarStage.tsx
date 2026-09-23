"use client";

import Image from "next/image";
import { motion } from "framer-motion";
import { Mic, User } from "lucide-react";
import { useState } from "react";

interface AvatarStageProps {
  speaking: boolean;
  listening: boolean;
  avatarSrc?: string;
  interviewerName?: string;
}

export function AvatarStage({
  speaking,
  listening,
  avatarSrc = "/avatars/interviewer.jpg",
  interviewerName = "AI Interviewer",
}: AvatarStageProps) {
  const [imgFailed, setImgFailed] = useState(false);

  return (
    <div className="relative flex aspect-video w-full items-center justify-center overflow-hidden rounded-lg bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900">
      {/* Speaking indicator glow */}
      {speaking ? (
        <motion.div
          initial={{ scale: 1, opacity: 0.4 }}
          animate={{ scale: [1, 1.35, 1], opacity: [0.4, 0.15, 0.4] }}
          transition={{ duration: 1.6, repeat: Infinity }}
          className="absolute inset-0 m-auto h-56 w-56 rounded-full bg-blue-500/40"
        />
      ) : null}

      {/* Listening indicator ring */}
      {listening && !speaking ? (
        <motion.div
          initial={{ scale: 1.2, opacity: 0.3 }}
          animate={{ scale: [1.2, 1.5, 1.2], opacity: [0.3, 0.05, 0.3] }}
          transition={{ duration: 2, repeat: Infinity }}
          className="absolute inset-0 m-auto h-56 w-56 rounded-full bg-green-500/40"
        />
      ) : null}

      {/* Avatar */}
      <motion.div
        animate={{
          scale: speaking ? [1, 1.03, 1] : 1,
          y: speaking ? [0, -4, 0] : 0,
        }}
        transition={{
          duration: 1.2,
          repeat: speaking ? Infinity : 0,
          ease: "easeInOut",
        }}
        className="relative z-10 h-48 w-48 overflow-hidden rounded-full border-4 border-white/20 shadow-2xl sm:h-56 sm:w-56"
      >
        {!imgFailed ? (
          <Image
            src={avatarSrc}
            alt={interviewerName}
            fill
            className="object-cover"
            priority
            unoptimized
            onError={() => setImgFailed(true)}
          />
        ) : (
          <div className="flex h-full w-full items-center justify-center bg-slate-700">
            <User className="h-24 w-24 text-slate-400" />
          </div>
        )}
      </motion.div>

      {/* Name badge */}
      <div className="absolute top-4 left-4 z-20">
        <div className="rounded-full bg-black/60 px-3 py-1 backdrop-blur">
          <span className="text-xs font-medium text-white">
            {interviewerName}
          </span>
        </div>
      </div>

      {/* Status label */}
      <div className="absolute bottom-4 left-1/2 z-20 -translate-x-1/2">
        <div className="flex items-center gap-2 rounded-full bg-black/60 px-4 py-1.5 backdrop-blur">
          {speaking ? (
            <>
              <div className="flex h-3 items-end gap-0.5">
                {[0, 1, 2].map((i) => (
                  <motion.span
                    key={i}
                    className="w-0.5 rounded-full bg-blue-400"
                    animate={{ height: [4, 12, 4] }}
                    transition={{
                      duration: 0.8,
                      repeat: Infinity,
                      delay: i * 0.15,
                    }}
                  />
                ))}
              </div>
              <span className="text-xs font-medium text-white">
                Speaking...
              </span>
            </>
          ) : listening ? (
            <>
              <Mic className="h-3 w-3 text-green-400" />
              <span className="text-xs font-medium text-white">
                Listening to you
              </span>
            </>
          ) : (
            <span className="text-xs font-medium text-white/70">
              Waiting for your answer
            </span>
          )}
        </div>
      </div>
    </div>
  );
}