package de.kazhal.quizbackend.question;

import java.time.Duration;
import java.util.List;
public record QuestionPlayResponse(
        Long id,
        String text,
        QuestionType type,
        Difficulty difficulty,
        int points,
        long timeLimitSeconds,
        List<OptionPlayResponse> options) {
    public static QuestionPlayResponse from(Question q, Duration defaultTimeLimit) {
        long timeLimit = q.getTimeLimitSeconds() != null
                ? q.getTimeLimitSeconds()
                : defaultTimeLimit.toSeconds();
        return new QuestionPlayResponse(q.getId(), q.getText(), q.getType(), q.getDifficulty(),
                q.getPoints(), timeLimit,
                q.getOptions().stream().map(OptionPlayResponse::from).toList());
    }
}