"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { cn } from "@/lib/utils";

const TABS = [
  { href: "/admin", label: "Statistics" },
  { href: "/admin/users", label: "Users" },
  { href: "/admin/interviews", label: "Interviews" },
  { href: "/admin/ai-usage", label: "AI usage" },
];

export function AdminTabs() {
  const pathname = usePathname();
  return (
    <nav className="flex flex-wrap gap-1 border-b pb-2">
      {TABS.map((tab) => {
        const active = pathname === tab.href;
        return (
          <Link
            key={tab.href}
            href={tab.href}
            className={cn(
              "rounded-md px-3 py-1.5 text-sm transition-colors",
              active
                ? "bg-primary/10 text-primary"
                : "text-muted-foreground hover:bg-accent",
            )}
          >
            {tab.label}
          </Link>
        );
      })}
    </nav>
  );
}