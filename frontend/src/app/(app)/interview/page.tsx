import Link from "next/link";
import { Button } from "@/components/ui/button";
import { InterviewList } from "@/components/interview/InterviewList";

export default function InterviewsPage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold">Interviews</h1>
          <p className="text-muted-foreground">
            Review your past sessions or start a new one.
          </p>
        </div>
        <Button asChild>
          <Link href="/interview/setup">New interview</Link>
        </Button>
      </div>
      <InterviewList />
    </div>
  );
}