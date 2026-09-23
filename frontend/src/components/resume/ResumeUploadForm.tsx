"use client";

import { useRef, useState } from "react";
import { UploadCloud } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Spinner } from "@/components/ui/spinner";
import { useUploadResume } from "@/hooks/useResumes";
import { cn } from "@/lib/utils";

const ACCEPTED = ".pdf,.docx";

interface ResumeUploadFormProps {
  onUploaded?: (resumeId: string) => void;
}

export function ResumeUploadForm({ onUploaded }: ResumeUploadFormProps) {
  const inputRef = useRef<HTMLInputElement | null>(null);
  const [dragging, setDragging] = useState(false);
  const upload = useUploadResume();

  const handleFiles = (files: FileList | null) => {
    if (!files || files.length === 0) return;
    const file = files[0];
    upload.mutate(file, {
      onSuccess: (data) => {
        onUploaded?.(data.resumeId);
      },
    });
    if (inputRef.current) inputRef.current.value = "";
  };

  return (
    <Card>
      <CardContent
        className={cn(
          "flex flex-col items-center justify-center gap-3 rounded-lg border-2 border-dashed p-10 text-center transition-colors",
          dragging ? "border-primary bg-primary/5" : "border-border",
        )}
        onDragOver={(e) => {
          e.preventDefault();
          setDragging(true);
        }}
        onDragLeave={() => setDragging(false)}
        onDrop={(e) => {
          e.preventDefault();
          setDragging(false);
          handleFiles(e.dataTransfer.files);
        }}
      >
        <UploadCloud className="h-10 w-10 text-muted-foreground" />
        <div>
          <p className="font-medium">Drag &amp; drop your resume</p>
          <p className="text-sm text-muted-foreground">
            PDF or DOCX up to 10&nbsp;MB
          </p>
        </div>
        <input
          ref={inputRef}
          type="file"
          accept={ACCEPTED}
          className="hidden"
          onChange={(e) => handleFiles(e.target.files)}
        />
        <Button
          type="button"
          variant="outline"
          disabled={upload.isPending}
          onClick={() => inputRef.current?.click()}
        >
          {upload.isPending ? <Spinner /> : "Browse files"}
        </Button>
      </CardContent>
    </Card>
  );
}