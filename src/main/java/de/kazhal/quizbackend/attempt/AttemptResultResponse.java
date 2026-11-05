package de.kazhal.quizbackend.attempt;

import java.time.Instant;
import java.util.List;
public record AttemptResultResponse(
        Long id,
        Long topicId,
        String topicName,
        Instant startedAt,
        Instant finishedAt,
        boolean finished,
        int totalScore,
        int maxScore,
        int percent,
        List<AnsweredQuestionResponse> answers) {
    public static AttemptResultResponse from(Attempt a) {
        return new AttemptResultResponse(
                a.getId(),
                a.getTopic().getId(),
                a.getTopic().getName(),
                a.getStartedAt(),
                a.getFinishedAt(),
                a.isFinished(),
                a.getTotalScore(),
                a.getMaxScore(),
                a.percent(),
                a.getAnswers().stream().map(AnsweredQuestionResponse::from).toList());
    }
}