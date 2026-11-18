package de.kazhal.quizbackend.attempt;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    @EntityGraph(attributePaths = {"topic", "answers", "answers.selectedOptions"})
    Optional<Attempt> findWithAnswersById(Long id);

    List<Attempt> findByTopicIdOrderByStartedAtDesc(Long topicId);
    boolean existsByTopicId(Long topicId);
    @Query("""
        select new de.kazhal.quizbackend.attempt.AttemptSummaryResponse(
        a.id, a.startedAt, a.finishedAt, a.totalScore, a.maxScore)
        from Attempt a
        where a.topic.id = :topicId and a.user.id = :userId
        order by a.startedAt desc
        """)
    List<AttemptSummaryResponse> findSummariesByTopicIdAndUserId(@Param("topicId") Long topicId,
                                                                 @Param("userId") Long userId);
}


