// package com.interviewai.util;

// import com.interviewai.dto.common.PageRequestParams;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;

// import java.util.Set;

// /**
//  * Builds a safe Spring {@link Pageable} from request params.
//  *
//  * A whitelist of allowed sort fields prevents clients from sorting on
//  * unindexed or sensitive fields (e.g., passwordHash, email).
//  */
// public final class PageRequestBuilder {

//     private PageRequestBuilder() {}

//     /**
//      * @param params      request params
//      * @param allowedSort whitelist of sort fields
//      * @param defaultSort default sort if none specified or invalid
//      * @param tieBreaker  field to append to every sort for stable ordering (usually "_id")
//      */
//     public static Pageable build(
//             PageRequestParams params,
//             Set<String> allowedSort,
//             Sort defaultSort,
//             String tieBreaker) {

//         int page = params.effectivePage();
//         int size = params.effectiveSize();

//         Sort sort = parseSort(params.getSort(), allowedSort, defaultSort);

//         // Append tie-breaker to guarantee stable ordering.
//         if (tieBreaker != null && !tieBreaker.isBlank()) {
//             boolean alreadyThere = sort.stream()
//                     .anyMatch(o -> o.getProperty().equals(tieBreaker));
//             if (!alreadyThere) {
//                 sort = sort.and(Sort.by(Sort.Direction.DESC, tieBreaker));
//             }
//         }

//         return PageRequest.of(page, size, sort);
//     }

//     private static Sort parseSort(String raw, Set<String> allowed, Sort fallback) {
//         if (raw == null || raw.isBlank()) return fallback;

//         String[] parts = raw.split(",", 2);
//         String field = parts[0].trim();
//         if (!allowed.contains(field)) return fallback;

//         Sort.Direction dir = parts.length > 1 && parts[1].trim().equalsIgnoreCase("asc")
//                 ? Sort.Direction.ASC
//                 : Sort.Direction.DESC;

//         return Sort.by(dir, field);
//     }
// }

package com.interviewai.util;

import com.interviewai.dto.common.PageRequestParams;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class PageRequestBuilder {

    private PageRequestBuilder() {}

    public static Pageable build(
            PageRequestParams params,
            Set<String> allowedSort,
            Sort defaultSort,
            String tieBreaker) {

        int page = params.effectivePage();
        int size = params.effectiveSize();

        Sort sort = parseSort(params.getSort(), allowedSort, defaultSort);

        if (tieBreaker != null && !tieBreaker.isBlank()) {
            boolean alreadyThere = sort.stream()
                    .anyMatch(o -> o.getProperty().equals(tieBreaker));
            if (!alreadyThere) {
                sort = sort.and(Sort.by(Sort.Direction.DESC, tieBreaker));
            }
        }

        return PageRequest.of(page, size, sort);
    }

    private static Sort parseSort(String raw, Set<String> allowed, Sort fallback) {
        if (raw == null || raw.isBlank()) return fallback;

        String[] parts = raw.split(",", 2);
        String field = parts[0].trim();
        if (!allowed.contains(field)) return fallback;

        Sort.Direction dir = parts.length > 1 && parts[1].trim().equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return Sort.by(dir, field);
    }
}