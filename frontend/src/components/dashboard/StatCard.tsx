import { Card, CardContent } from "@/components/ui/card";
import { cn } from "@/lib/utils";

interface StatCardProps {
  label: string;
  value: string | number;
  hint?: string;
  accent?: "default" | "success" | "warning" | "destructive";
  className?: string;
}

const accentClass: Record<NonNullable<StatCardProps["accent"]>, string> = {
  default: "text-foreground",
  success: "text-emerald-600",
  warning: "text-amber-600",
  destructive: "text-destructive",
};

export function StatCard({
  label,
  value,
  hint,
  accent = "default",
  className,
}: StatCardProps) {
  return (
    <Card className={className}>
      <CardContent className="p-5">
        <p className="text-sm text-muted-foreground">{label}</p>
        <p className={cn("mt-1 text-3xl font-bold", accentClass[accent])}>
          {value}
        </p>
        {hint ? (
          <p className="mt-1 text-xs text-muted-foreground">{hint}</p>
        ) : null}
      </CardContent>
    </Card>
  );
}