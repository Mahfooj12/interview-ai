"use client";

import { useInfiniteQuery, useQuery } from "@tanstack/react-query";
import { interviewApi } from "@/lib/api/interview";

export const interviewKeys = {
  all: ["interviews"] as const,
  paginated: (limit: number) => ["interviews", "paginated", limit] as const,
};

/**
 * Legacy unpaginated hook — returns all interviews.
 * Prefer useInterviewsPaginated for anything that displays a list.
 */
export function useInterviews() {
  return useQuery({
    queryKey: interviewKeys.all,
    queryFn: async () => {
      const page = await interviewApi.listPaginated({ limit: 100 });
      return page.items;
    },
  });
}

/**
 * Cursor-paginated infinite query.
 * Pages are fetched on demand via fetchNextPage().
 */
export function useInterviewsPaginated(limit = 20) {
  return useInfiniteQuery({
    queryKey: interviewKeys.paginated(limit),
    queryFn: ({ pageParam }) =>
      interviewApi.listPaginated({
        limit,
        cursor: pageParam as string | null,
      }),
    initialPageParam: null as string | null,
    getNextPageParam: (lastPage) =>
      lastPage.hasMore ? lastPage.nextCursor : undefined,
  });
}