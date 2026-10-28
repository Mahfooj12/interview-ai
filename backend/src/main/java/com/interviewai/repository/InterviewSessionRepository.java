package com.interviewai.repository;

import com.interviewai.model.InterviewSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewSessionRepository extends MongoRepository<InterviewSession, String> {

    @Query("{ 'userId': ?0 }")
    List<InterviewSession> findAllByUserId(String userId, Sort sort);

    default List<InterviewSession> findAllByUserIdOrderByStartedAtDesc(String userId) {
        return findAllByUserId(userId, Sort.by(Sort.Direction.DESC, "startedAt"));
    }

    Optional<InterviewSession> findByIdAndUserId(String id, String userId);

    long countByUserId(String userId);

    long countByStatus(InterviewSession.Status status);

    // ------------------------------------------------------------------
    // CURSOR PAGINATION
    // ------------------------------------------------------------------

    /**
     * Fetch first page of a user's interviews, sorted by startedAt DESC, id DESC.
     * Uses the user_started_idx index.
     *
     * @param userId the owning user
     * @param limit  max items to return (caller must add 1 to detect hasMore)
     */
    @Query(value = "{ 'userId': ?0 }", sort = "{ 'startedAt': -1, '_id': -1 }")
    List<InterviewSession> findFirstPage(String userId, org.springframework.data.domain.Pageable pageable);

    /**
     * Fetch next page after a cursor. Uses $or because MongoDB has no
     * native tuple comparison — we compare (startedAt < X) OR
     * (startedAt == X AND _id < Y).
     *
     * @param userId    the owning user
     * @param startedAt cursor's startedAt value
     * @param id        cursor's id value
     */
    @Query(value = """
        {
          'userId': ?0,
          '$or': [
            { 'startedAt': { '$lt': ?1 } },
            { 'startedAt': ?1, '_id': { '$lt': ?2 } }
          ]
        }
        """, sort = "{ 'startedAt': -1, '_id': -1 }")
    List<InterviewSession> findAfterCursor(
            String userId,
            Instant startedAt,
            String id,
            org.springframework.data.domain.Pageable pageable
    );
    Page<InterviewSession> findAllBy(Pageable pageable);
}