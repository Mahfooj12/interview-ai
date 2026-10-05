package com.interviewai.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Generic cursor-paginated response wrapper.
 *
 * @param <T> the item type in the page
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursorPage<T> {

    /** Items in this page. May be smaller than the requested limit on the last page. */
    private List<T> items;

    /**
     * Opaque cursor to fetch the next page.
     * Null when this is the last page.
     */
    private String nextCursor;

    /** True if more items exist after this page. */
    private boolean hasMore;

    /** The limit that was applied (may differ from requested if clamped). */
    private int limit;
}
