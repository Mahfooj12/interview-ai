// import { ProtectedRoute } from "@/components/auth/ProtectedRoute";
// import { AppNavbar } from "@/components/layout/AppNavbar";
// import { AppSidebar } from "@/components/layout/AppSidebar";

// export default function AppLayout({
//   children,
// }: {
//   children: React.ReactNode;
// }) {
//   return (
//     <ProtectedRoute>
//       <div className="flex min-h-screen flex-col">
//         <AppNavbar />
//         <div className="flex flex-1">
//           <AppSidebar />
//           <main className="flex-1 p-6">{children}</main>
//         </div>
//       </div>
//     </ProtectedRoute>
//   );
// }
import { ProtectedRoute } from "@/components/auth/ProtectedRoute";
import { AppNavbar } from "@/components/layout/AppNavbar";
import { AppSidebar } from "@/components/layout/AppSidebar";

export default function AppLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <ProtectedRoute>
      <div className="flex min-h-screen bg-background">
        <AppSidebar />
        <div className="flex min-h-screen flex-1 flex-col">
          <AppNavbar />
          <main className="flex-1 animate-fade-in-up px-4 py-6 sm:px-6 lg:px-8">
            <div className="mx-auto w-full max-w-6xl">{children}</div>
          </main>
        </div>
      </div>
    </ProtectedRoute>
  );
}