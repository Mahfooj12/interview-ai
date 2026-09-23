import Link from "next/link";
import { Button } from "@/components/ui/button";
import { JobCreateForm } from "@/components/job/JobCreateForm";

export default function NewJobPage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold">New job description</h1>
          <p className="text-muted-foreground">
            Paste the job description — we extract required skills and
            technologies.
          </p>
        </div>
        <Button asChild variant="ghost">
          <Link href="/jobs">Back</Link>
        </Button>
      </div>
      <JobCreateForm />
    </div>
  );
}