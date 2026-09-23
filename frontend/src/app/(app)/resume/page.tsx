import Link from "next/link";
import { Button } from "@/components/ui/button";
import { ResumeList } from "@/components/resume/ResumeList";

export default function ResumePage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold">Resumes</h1>
          <p className="text-muted-foreground">
            Manage your resumes and trigger AI analysis.
          </p>
        </div>
        <Button asChild>
          <Link href="/resume/upload">Upload resume</Link>
        </Button>
      </div>
      <ResumeList />
    </div>
  );
}