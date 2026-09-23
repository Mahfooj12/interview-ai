"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  LayoutDashboard,
  FileText,
  Briefcase,
  Mic,
  BarChart3,
  User,
  ShieldCheck,
} from "lucide-react";
import { cn } from "@/lib/utils";
import { useAuthStore } from "@/stores/auth-store";

const baseLinks = [
  { href: "/dashboard", label: "Dashboard", icon: LayoutDashboard },
  { href: "/resume", label: "Resumes", icon: FileText },
  { href: "/jobs", label: "Jobs", icon: Briefcase },
  { href: "/interview/setup", label: "New interview", icon: Mic },
  { href: "/reports", label: "Reports", icon: BarChart3 },
  { href: "/profile", label: "Profile", icon: User },
];

export function AppSidebar() {
  const pathname = usePathname();
  const user = useAuthStore((s) => s.user);
  const isAdmin = user?.roles?.includes("ADMIN") ?? false;

  const links = isAdmin
    ? [...baseLinks, { href: "/admin", label: "Admin", icon: ShieldCheck }]
    : baseLinks;

  return (
    <aside className="sticky top-0 hidden h-screen w-60 shrink-0 overflow-y-auto border-r border-border/70 bg-card/50 backdrop-blur-sm md:flex md:flex-col">
      {/* Brand */}
      <div className="flex h-16 shrink-0 items-center gap-2.5 border-b border-border/70 px-5">
        <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-gradient-to-br from-primary to-primary/70 text-primary-foreground shadow-soft">
          <Mic className="h-4 w-4" />
        </div>
        <Link
          href="/dashboard"
          className="text-[15px] font-semibold tracking-tight text-foreground"
        >
          InterviewAI
        </Link>
      </div>

      {/* Nav */}
      <nav className="flex flex-1 flex-col gap-0.5 p-3">
        {links.map(({ href, label, icon: Icon }) => {
          const active =
            pathname === href ||
            (href !== "/dashboard" &&
              href !== "/interview/setup" &&
              pathname.startsWith(href));

          return (
            <Link
              key={href}
              href={href}
              className={cn(
                "group relative flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-all duration-200",
                active
                  ? "bg-accent text-accent-foreground shadow-soft"
                  : "text-muted-foreground hover:bg-muted/60 hover:text-foreground"
              )}
            >
              {/* Active indicator bar */}
              {active ? (
                <span className="absolute left-0 top-1/2 h-5 w-0.5 -translate-y-1/2 rounded-r-full bg-primary" />
              ) : null}

              <Icon
                className={cn(
                  "h-4 w-4 shrink-0 transition-colors",
                  active
                    ? "text-primary"
                    : "text-muted-foreground group-hover:text-foreground"
                )}
              />
              <span className="truncate">{label}</span>
            </Link>
          );
        })}
      </nav>

      {/* Footer — plan badge */}
      <div className="shrink-0 border-t border-border/70 p-3">
        <div className="rounded-lg bg-gradient-to-br from-primary/10 to-primary/5 p-3">
          <p className="text-xs font-medium text-foreground">
            {user?.plan ?? "FREE"} Plan
          </p>
          <p className="mt-0.5 text-[11px] text-muted-foreground">
            {user?.email ?? ""}
          </p>
        </div>
      </div>
    </aside>
  );
}