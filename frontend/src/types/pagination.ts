/**
 * Generic paginated response envelope returned by paginated API endpoints.
 * Mirrors the backend's `PageResponse<T>` DTO.
 */
export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  sort: string | null;
}

export interface PaginationParams {
  page?: number;
  size?: number;
  sort?: string;
}

export const DEFAULT_PAGE_SIZE = 20;
export const MAX_PAGE_SIZE = 100;
