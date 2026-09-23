import Link from "next/link";
import { Button } from "@/components/ui/button";

export default function HomePage() {
  return (
    <main className="mx-auto flex min-h-screen max-w-5xl flex-col items-center justify-center gap-8 px-6 text-center">
      <div className="space-y-4">
        <h1 className="text-4xl font-bold tracking-tight sm:text-6xl">
          Ace your next interview with AI
        </h1>
        <p className="text-lg text-muted-foreground">
          Upload your resume, choose a role, and practice adaptive mock
          interviews with instant feedback.
        </p>
      </div>
      <div className="flex gap-4">
        <Button asChild size="lg">
          <Link href="/register">Get started</Link>
        </Button>
        <Button asChild size="lg" variant="outline">
          <Link href="/login">Log in</Link>
        </Button>
      </div>
    </main>
  );
}