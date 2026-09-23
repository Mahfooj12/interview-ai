"use client";

import { useEffect } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { AnimatePresence, motion } from "framer-motion";
import {
  LayoutDashboard,
  FileText,
  Briefcase,
  Mic,
  BarChart3,
  User,
  ShieldCheck,
  X,
} from "lucide-react";

import { cn } from "@/lib/utils";
import { useAuthStore } from "@/stores/auth-store";
import { useUiStore } from "@/stores/ui-store";

const baseLinks = [
  { href: "/dashboard", label: "Dashboard", icon: LayoutDashboard },
  { href: "/resume", label: "Resumes", icon: FileText },
  { href: "/jobs", label: "Jobs", icon: Briefcase },
  { href: "/interview/setup", label: "New interview", icon: Mic },
  { href: "/reports", label: "Reports", icon: BarChart3 },
  { href: "/profile", label: "Profile", icon: User },
];

export function MobileSidebar() {
  const pathname = usePathname();
  const user = useAuthStore((s) => s.user);
  const isAdmin = user?.roles?.includes("ADMIN") ?? false;
  const open = useUiStore((s) => s.mobileSidebarOpen);
  const close = useUiStore((s) => s.closeMobileSidebar);

  const links = isAdmin
    ? [...baseLinks, { href: "/admin", label: "Admin", icon: ShieldCheck }]
    : baseLinks;

  // Close on route change
  useEffect(() => {
    close();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pathname]);

  // Lock body scroll while open
  useEffect(() => {
    if (open) {
      const original = document.body.style.overflow;
      document.body.style.overflow = "hidden";
      return () => {
        document.body.style.overflow = original;
      };
    }
  }, [open]);

  // Close on Escape
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (e.key === "Escape") close();
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [close]);

  return (
    <AnimatePresence>
      {open ? (
        <>
          {/* Backdrop */}
          <motion.div
            key="backdrop"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.2 }}
            onClick={close}
            className="fixed inset-0 z-50 bg-black/40 backdrop-blur-sm md:hidden"
          />

          {/* Drawer */}
          <motion.aside
            key="drawer"
            initial={{ x: "-100%" }}
            animate={{ x: 0 }}
            exit={{ x: "-100%" }}
            transition={{ duration: 0.25, ease: [0.16, 1, 0.3, 1] }}
            className="fixed inset-y-0 left-0 z-50 flex w-64 flex-col bg-card shadow-floating md:hidden"
          >
            {/* Brand + close */}
            <div className="flex h-16 shrink-0 items-center justify-between border-b border-border/70 px-5">
              <div className="flex items-center gap-2.5">
                <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-gradient-to-br from-primary to-primary/70 text-primary-foreground shadow-soft">
                  <Mic className="h-4 w-4" />
                </div>
                <span className="text-[15px] font-semibold tracking-tight">
                  InterviewAI
                </span>
              </div>
              <button
                onClick={close}
                className="rounded-full p-1.5 text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
                aria-label="Close menu"
              >
                <X className="h-4 w-4" />
              </button>
            </div>

            {/* Nav */}
            <nav className="flex flex-1 flex-col gap-0.5 overflow-y-auto p-3">
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
                    onClick={close}
                    className={cn(
                      "group relative flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-all duration-200",
                      active
                        ? "bg-accent text-accent-foreground shadow-soft"
                        : "text-muted-foreground hover:bg-muted/60 hover:text-foreground"
                    )}
                  >
                    {active ? (
                      <span className="absolute left-0 top-1/2 h-5 w-0.5 -translate-y-1/2 rounded-r-full bg-primary" />
                    ) : null}
                    <Icon
                      className={cn(
                        "h-4 w-4 shrink-0",
                        active ? "text-primary" : ""
                      )}
                    />
                    <span className="truncate">{label}</span>
                  </Link>
                );
              })}
            </nav>

            {/* Plan badge */}
            <div className="shrink-0 border-t border-border/70 p-3">
              <div className="rounded-lg bg-gradient-to-br from-primary/10 to-primary/5 p-3">
                <p className="text-xs font-medium text-foreground">
                  {user?.plan ?? "FREE"} Plan
                </p>
                <p className="mt-0.5 truncate text-[11px] text-muted-foreground">
                  {user?.email ?? ""}
                </p>
              </div>
            </div>
          </motion.aside>
        </>
      ) : null}
    </AnimatePresence>
  );
}