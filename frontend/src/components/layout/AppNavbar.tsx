// "use client";

// import Link from "next/link";
// import { useRouter } from "next/navigation";
// import { useEffect, useState } from "react";
// import { LogOut, User as UserIcon } from "lucide-react";
// import { toast } from "sonner";

// import { Button } from "@/components/ui/button";
// import { authApi } from "@/lib/api/auth";
// import { tokenStore } from "@/lib/auth/token-store";
// import { useAuthStore } from "@/stores/auth-store";
// import { cn } from "@/lib/utils";

// export function AppNavbar() {
//   const router = useRouter();
//   const { user, clear } = useAuthStore();
//   const [scrolled, setScrolled] = useState(false);

//   useEffect(() => {
//     const onScroll = () => setScrolled(window.scrollY > 4);
//     onScroll();
//     window.addEventListener("scroll", onScroll, { passive: true });
//     return () => window.removeEventListener("scroll", onScroll);
//   }, []);

//   const handleLogout = async () => {
//     const refreshToken = tokenStore.getRefreshToken();
//     try {
//       if (refreshToken) await authApi.logout(refreshToken);
//     } catch {
//       // ignore
//     } finally {
//       clear();
//       toast.success("Logged out");
//       router.push("/login");
//     }
//   };

//   return (
//     <header
//       className={cn(
//         "sticky top-0 z-40 flex h-16 shrink-0 items-center justify-between border-b px-4 transition-all duration-200 sm:px-6",
//         scrolled
//           ? "border-border/70 bg-background/80 backdrop-blur-md scroll-shadow"
//           : "border-transparent bg-background"
//       )}
//     >
//       {/* Left — mobile brand */}
//       <Link
//         href="/dashboard"
//         className="text-base font-semibold tracking-tight md:hidden"
//       >
//         InterviewAI
//       </Link>

//       {/* Left spacer on desktop */}
//       <div className="hidden md:block" />

//       {/* Right */}
//       <div className="flex items-center gap-2">
//         <span className="hidden text-sm text-muted-foreground sm:inline">
//           {user?.email}
//         </span>
//         <Button
//           variant="ghost"
//           size="icon"
//           asChild
//           className="rounded-full transition-transform duration-200 hover:scale-105"
//         >
//           <Link href="/profile" aria-label="Profile">
//             <UserIcon className="h-4 w-4" />
//           </Link>
//         </Button>
//         <Button
//           variant="ghost"
//           size="icon"
//           onClick={handleLogout}
//           aria-label="Log out"
//           className="rounded-full transition-transform duration-200 hover:scale-105"
//         >
//           <LogOut className="h-4 w-4" />
//         </Button>
//       </div>
//     </header>
//   );
// }

"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { User as UserIcon } from "lucide-react";

import { Button } from "@/components/ui/button";
import { MobileMenu } from "@/components/layout/MobileMenu";
import { useAuthStore } from "@/stores/auth-store";
import { cn } from "@/lib/utils";

export function AppNavbar() {
  const router = useRouter();
  const user = useAuthStore((s) => s.user);
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 4);
    onScroll();
    window.addEventListener("scroll", onScroll, { passive: true });
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  return (
    <header
      className={cn(
        "sticky top-0 z-40 flex h-16 shrink-0 items-center justify-between border-b px-3 transition-all duration-200 sm:px-6",
        scrolled
          ? "border-border/70 bg-background/80 backdrop-blur-md scroll-shadow"
          : "border-transparent bg-background"
      )}
    >
      {/* Brand — mobile only (desktop has it in sidebar) */}
      <Link
        href="/dashboard"
        className="text-base font-semibold tracking-tight md:hidden"
      >
        InterviewAI
      </Link>

      {/* Desktop spacer */}
      <div className="hidden md:block" />

      {/* Right side — email (desktop), profile icon, and 3-dot menu (mobile) */}
      <div className="flex items-center gap-1.5 sm:gap-2">
        <span className="hidden text-sm text-muted-foreground sm:inline">
          {user?.email}
        </span>

        <Button
          variant="ghost"
          size="icon"
          asChild
          className="hidden rounded-full transition-transform duration-200 hover:scale-105 md:inline-flex"
        >
          <Link href="/profile" aria-label="Profile">
            <UserIcon className="h-4 w-4" />
          </Link>
        </Button>

        {/* 3-dot menu — mobile only */}
        <MobileMenu />
      </div>
    </header>
  );
}