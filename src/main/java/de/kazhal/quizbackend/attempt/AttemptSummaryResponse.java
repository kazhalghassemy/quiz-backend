package de.kazhal.quizbackend.attempt;

import java.time.Instant;

public record AttemptSummaryResponse(
        Long id,
        Instant startedAt,
        Instant finishedAt,
        boolean finished,
        int totalScore,
        int maxScore,
        int percent) {
    /**
     * Wird von der JPQL-Abfrage aufgerufen (select new ...).
     */
    public AttemptSummaryResponse(Long id, Instant startedAt, Instant finishedAt,
                                  Integer totalScore, Integer maxScore) {
        this(id, startedAt, finishedAt, finishedAt != null, totalScore, maxScore,
                maxScore == 0 ? 0 : totalScore * 100 / maxScore);
    }
}