"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Spinner } from "@/components/ui/spinner";

interface EndInterviewDialogProps {
  onConfirm: () => void | Promise<void>;
  ending: boolean;
}

export function EndInterviewDialog({
  onConfirm,
  ending,
}: EndInterviewDialogProps) {
  const [open, setOpen] = useState(false);

  return (
    <>
      <Button
        variant="destructive"
        onClick={() => setOpen(true)}
        disabled={ending}
      >
        End interview
      </Button>

      {open ? (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
          <div className="w-full max-w-sm rounded-lg bg-background p-5 shadow-xl">
            <h2 className="text-lg font-semibold">End this interview?</h2>
            <p className="mt-1 text-sm text-muted-foreground">
              The AI will generate a report using the answers you've submitted
              so far.
            </p>
            <div className="mt-4 flex justify-end gap-2">
              <Button
                variant="ghost"
                onClick={() => setOpen(false)}
                disabled={ending}
              >
                Cancel
              </Button>
              <Button
                variant="destructive"
                onClick={async () => {
                  await onConfirm();
                  setOpen(false);
                }}
                disabled={ending}
              >
                {ending ? <Spinner /> : "End interview"}
              </Button>
            </div>
          </div>
        </div>
      ) : null}
    </>
  );
}