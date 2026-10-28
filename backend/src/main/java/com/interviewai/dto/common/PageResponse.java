// package com.interviewai.dto.common;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;
// import org.springframework.data.domain.Page;

// import java.util.List;
// import java.util.function.Function;

// @Getter
// @Setter
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// public class PageResponse<T> {

//     private List<T> content;
//     private int page;
//     private int size;
//     private long totalElements;
//     private int totalPages;
//     private boolean first;
//     private boolean last;
//     private String sort;

//     public static <T> PageResponse<T> from(Page<T> springPage) {
//         return PageResponse.<T>builder()
//                 .content(springPage.getContent())
//                 .page(springPage.getNumber())
//                 .size(springPage.getSize())
//                 .totalElements(springPage.getTotalElements())
//                 .totalPages(springPage.getTotalPages())
//                 .first(springPage.isFirst())
//                 .last(springPage.isLast())
//                 .sort(springPage.getSort().isSorted()
//                         ? springPage.getSort().toString()
//                         : null)
//                 .build();
//     }

//     public static <S, T> PageResponse<T> from(Page<S> springPage, Function<S, T> mapper) {
//         List<T> mapped = springPage.getContent().stream().map(mapper).toList();
//         return PageResponse.<T>builder()
//                 .content(mapped)
//                 .page(springPage.getNumber())
//                 .size(springPage.getSize())
//                 .totalElements(springPage.getTotalElements())
//                 .totalPages(springPage.getTotalPages())
//                 .first(springPage.isFirst())
//                 .last(springPage.isLast())
//                 .sort(springPage.getSort().isSorted()
//                         ? springPage.getSort().toString()
//                         : null)
//                 .build();
//     }
// }

package com.interviewai.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private String sort;

    public static <T> PageResponse<T> from(Page<T> springPage) {
        return PageResponse.<T>builder()
                .content(springPage.getContent())
                .page(springPage.getNumber())
                .size(springPage.getSize())
                .totalElements(springPage.getTotalElements())
                .totalPages(springPage.getTotalPages())
                .first(springPage.isFirst())
                .last(springPage.isLast())
                .sort(springPage.getSort().isSorted() ? springPage.getSort().toString() : null)
                .build();
    }

    public static <S, T> PageResponse<T> from(Page<S> springPage, Function<S, T> mapper) {
        List<T> mapped = springPage.getContent().stream().map(mapper).toList();
        return PageResponse.<T>builder()
                .content(mapped)
                .page(springPage.getNumber())
                .size(springPage.getSize())
                .totalElements(springPage.getTotalElements())
                .totalPages(springPage.getTotalPages())
                .first(springPage.isFirst())
                .last(springPage.isLast())
                .sort(springPage.getSort().isSorted() ? springPage.getSort().toString() : null)
                .build();
    }
}