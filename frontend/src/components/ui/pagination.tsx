"use client";

import { ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

interface PaginationProps {
  page: number;
  totalPages: number;
  totalElements: number;
  size: number;
  onPageChange: (page: number) => void;
  className?: string;
}

export function Pagination({
  page,
  totalPages,
  totalElements,
  size,
  onPageChange,
  className,
}: PaginationProps) {
  if (totalPages <= 1) return null;

  const firstIndex = page * size + 1;
  const lastIndex = Math.min((page + 1) * size, totalElements);

  const canPrev = page > 0;
  const canNext = page < totalPages - 1;

  return (
    <div
      className={cn(
        "flex flex-wrap items-center justify-between gap-3 pt-4",
        className,
      )}
    >
      <p className="text-xs text-muted-foreground">
        Showing <span className="font-medium">{firstIndex}</span>–
        <span className="font-medium">{lastIndex}</span> of{" "}
        <span className="font-medium">{totalElements}</span>
      </p>

      <div className="flex items-center gap-2">
        <Button
          variant="outline"
          size="sm"
          disabled={!canPrev}
          onClick={() => onPageChange(page - 1)}
        >
          <ChevronLeft className="mr-1 h-4 w-4" />
          Previous
        </Button>

        <span className="text-xs text-muted-foreground">
          Page <span className="font-medium">{page + 1}</span> of{" "}
          <span className="font-medium">{totalPages}</span>
        </span>

        <Button
          variant="outline"
          size="sm"
          disabled={!canNext}
          onClick={() => onPageChange(page + 1)}
        >
          Next
          <ChevronRight className="ml-1 h-4 w-4" />
        </Button>
      </div>
    </div>
  );
}