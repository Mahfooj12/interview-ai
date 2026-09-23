import Link from "next/link";
import { Button } from "@/components/ui/button";
import { JobList } from "@/components/job/JobList";

export default function JobsPage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold">Job descriptions</h1>
          <p className="text-muted-foreground">
            Parse job descriptions and match them against your resumes.
          </p>
        </div>
        <Button asChild>
          <Link href="/jobs/new">New job description</Link>
        </Button>
      </div>
      <JobList />
    </div>
  );
}