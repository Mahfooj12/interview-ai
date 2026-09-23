"use client";

import { useEffect, useRef } from "react";
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
  LogOut,
  MoreVertical,
} from "lucide-react";
import { toast } from "sonner";
import { useRouter } from "next/navigation";

import { cn } from "@/lib/utils";
import { useAuthStore } from "@/stores/auth-store";
import { useUiStore } from "@/stores/ui-store";
import { authApi } from "@/lib/api/auth";
import { tokenStore } from "@/lib/auth/token-store";

const baseLinks = [
  { href: "/dashboard", label: "Dashboard", icon: LayoutDashboard },
  { href: "/resume", label: "Resumes", icon: FileText },
  { href: "/jobs", label: "Jobs", icon: Briefcase },
  { href: "/interview/setup", label: "New interview", icon: Mic },
  { href: "/reports", label: "Reports", icon: BarChart3 },
  { href: "/profile", label: "Profile", icon: User },
];

export function MobileMenu() {
  const pathname = usePathname();
  const router = useRouter();
  const user = useAuthStore((s) => s.user);
  const clearAuth = useAuthStore((s) => s.clear);
  const isAdmin = user?.roles?.includes("ADMIN") ?? false;
  const open = useUiStore((s) => s.mobileSidebarOpen);
  const toggle = useUiStore((s) => s.toggleMobileSidebar);
  const close = useUiStore((s) => s.closeMobileSidebar);

  const containerRef = useRef<HTMLDivElement>(null);

  const links = isAdmin
    ? [...baseLinks, { href: "/admin", label: "Admin", icon: ShieldCheck }]
    : baseLinks;

  // Close on route change
  useEffect(() => {
    close();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pathname]);

  // Close on outside click
  useEffect(() => {
    if (!open) return;
    const onDown = (e: MouseEvent) => {
      if (
        containerRef.current &&
        !containerRef.current.contains(e.target as Node)
      ) {
        close();
      }
    };
    document.addEventListener("mousedown", onDown);
    return () => document.removeEventListener("mousedown", onDown);
  }, [open, close]);

  // Close on Escape
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (e.key === "Escape") close();
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [close]);

  const handleLogout = async () => {
    const refreshToken = tokenStore.getRefreshToken();
    try {
      if (refreshToken) await authApi.logout(refreshToken);
    } catch {
      // ignore
    } finally {
      clearAuth();
      toast.success("Logged out");
      close();
      router.push("/login");
    }
  };

  return (
    <div ref={containerRef} className="relative md:hidden">
      {/* 3-dot trigger */}
      <button
        onClick={toggle}
        aria-label="Open menu"
        aria-expanded={open}
        className={cn(
          "flex h-9 w-9 items-center justify-center rounded-lg transition-colors",
          open
            ? "bg-accent text-accent-foreground"
            : "text-muted-foreground hover:bg-muted hover:text-foreground"
        )}
      >
        <MoreVertical className="h-5 w-5" />
      </button>

      {/* Dropdown */}
      <AnimatePresence>
        {open ? (
          <motion.div
            initial={{ opacity: 0, y: -6, scale: 0.98 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: -6, scale: 0.98 }}
            transition={{ duration: 0.18, ease: [0.16, 1, 0.3, 1] }}
            className="absolute right-0 top-11 z-50 w-60 origin-top-right overflow-hidden rounded-xl border border-border/70 bg-card shadow-floating"
          >
            {/* User header */}
            <div className="border-b border-border/70 bg-gradient-to-br from-primary/5 to-transparent px-3.5 py-3">
              <p className="truncate text-sm font-medium text-foreground">
                {user?.name ?? "User"}
              </p>
              <p className="truncate text-xs text-muted-foreground">
                {user?.email ?? ""}
              </p>
            </div>

            {/* Links */}
            <nav className="flex flex-col p-1.5">
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
                      "flex items-center gap-3 rounded-md px-2.5 py-2 text-sm transition-colors",
                      active
                        ? "bg-accent text-accent-foreground"
                        : "text-muted-foreground hover:bg-muted hover:text-foreground"
                    )}
                  >
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

            {/* Logout */}
            <div className="border-t border-border/70 p-1.5">
              <button
                onClick={handleLogout}
                className="flex w-full items-center gap-3 rounded-md px-2.5 py-2 text-sm text-muted-foreground transition-colors hover:bg-destructive/10 hover:text-destructive"
              >
                <LogOut className="h-4 w-4 shrink-0" />
                <span>Log out</span>
              </button>
            </div>
          </motion.div>
        ) : null}
      </AnimatePresence>
    </div>
  );
}