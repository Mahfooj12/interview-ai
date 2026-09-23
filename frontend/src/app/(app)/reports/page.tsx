import { ReportList } from "@/components/report/ReportList";

export default function ReportsPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold">Reports</h1>
        <p className="text-muted-foreground">
          Detailed feedback for every completed interview.
        </p>
      </div>
      <ReportList />
    </div>
  );
}