package com.interviewai.dto.interview;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewPageRequest {

    public static final int DEFAULT_LIMIT = 20;
    public static final int MAX_LIMIT = 100;

    @Min(value = 1, message = "Limit must be at least 1")
    @Max(value = MAX_LIMIT, message = "Limit must not exceed " + MAX_LIMIT)
    private Integer limit;

    /** Opaque cursor from a previous page. Null for the first page. */
    private String cursor;

    /**
     * Returns the effective limit, clamped to the max if the user passes a larger value.
     * This is called from the service layer, not the controller, so validation
     * can be overridden by clamping behaviour.
     */
    public int effectiveLimit() {
        if (limit == null) return DEFAULT_LIMIT;
        return Math.min(limit, MAX_LIMIT);
    }
}
