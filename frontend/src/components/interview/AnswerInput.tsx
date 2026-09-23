"use client";

import { useState } from "react";
import { Send } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Spinner } from "@/components/ui/spinner";

interface AnswerInputProps {
  onSubmit: (text: string) => void;
  submitting: boolean;
  disabled?: boolean;
}

export function AnswerInput({
  onSubmit,
  submitting,
  disabled,
}: AnswerInputProps) {
  const [text, setText] = useState("");

  const handleSubmit = () => {
    if (!text.trim()) return;
    onSubmit(text.trim());
    setText("");
  };

  return (
    <div className="space-y-2">
      <Textarea
        value={text}
        onChange={(e) => setText(e.target.value)}
        placeholder="Type your answer here…"
        rows={6}
        disabled={disabled || submitting}
        onKeyDown={(e) => {
          if ((e.metaKey || e.ctrlKey) && e.key === "Enter") {
            handleSubmit();
          }
        }}
      />
      <div className="flex items-center justify-between">
        <p className="text-xs text-muted-foreground">
          Press ⌘/Ctrl + Enter to submit
        </p>
        <Button
          onClick={handleSubmit}
          disabled={disabled || submitting || !text.trim()}
        >
          {submitting ? <Spinner /> : (
            <>
              <Send className="mr-2 h-4 w-4" />
              Submit answer
            </>
          )}
        </Button>
      </div>
    </div>
  );
}