import { Suspense } from "react";
import { InterviewSetupForm } from "@/components/interview/InterviewSetupForm";
import { Skeleton } from "@/components/ui/skeleton";

export default function InterviewSetupPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold">Configure your interview</h1>
        <p className="text-muted-foreground">
          Choose the type, mode and difficulty. The AI adapts as you answer.
        </p>
      </div>

      <Suspense fallback={<Skeleton className="h-40 w-full" />}>
        <InterviewSetupForm />
      </Suspense>
    </div>
  );
}