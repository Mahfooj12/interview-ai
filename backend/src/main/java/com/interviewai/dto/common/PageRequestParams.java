package com.interviewai.dto.common;

//import jakarta.validation.constraints.Max;
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
public class PageRequestParams {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    @Min(0)
    @Builder.Default
    private Integer page = DEFAULT_PAGE;

    @Min(1)
    @Builder.Default
    private Integer size = DEFAULT_SIZE;

    /**
     * Sort expression: "field,direction" e.g. "createdAt,desc"
     */
    private String sort;

    public int effectivePage() {
        return page == null || page < 0 ? DEFAULT_PAGE : page;
    }

    public int effectiveSize() {
        if (size == null || size < 1) return DEFAULT_SIZE;
        return Math.min(size, MAX_SIZE);
    }
}