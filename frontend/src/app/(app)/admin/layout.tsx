import { AdminGuard } from "@/components/admin/AdminGuard";
import { AdminTabs } from "@/components/admin/AdminTabs";

export default function AdminLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <AdminGuard>
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-semibold">Admin</h1>
          <p className="text-muted-foreground">
            Platform statistics, users, interviews, and AI usage.
          </p>
        </div>
        <AdminTabs />
        {children}
      </div>
    </AdminGuard>
  );
}