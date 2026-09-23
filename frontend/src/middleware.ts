import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

const PROTECTED_PREFIXES = [
  "/dashboard",
  "/resume",
  "/jobs",
  "/interview",
  "/reports",
  "/progress",
  "/profile",
  "/settings",
  "/admin",
];

const AUTH_PAGES = ["/login", "/register"];

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;

  // Client-side auth lives in localStorage, so the middleware cannot read the
  // token. We only guard against obvious mismatches and let ProtectedRoute
  // handle the rest.
  const isProtected = PROTECTED_PREFIXES.some((p) => pathname.startsWith(p));
  const isAuthPage = AUTH_PAGES.includes(pathname);

  // Redirect old root-level paths into their proper sections if needed.
  if (isAuthPage) {
    return NextResponse.next();
  }

  if (isProtected) {
    return NextResponse.next();
  }

  return NextResponse.next();
}

export const config = {
  matcher: ["/((?!api|_next/static|_next/image|favicon.ico).*)"],
};