package de.kazhal.quizbackend.question;

import java.util.List;
public record QuestionRequest(
        String text,
        QuestionType type,
        Difficulty difficulty,
        int points,
        Integer timeLimitSeconds,
        String explanation,
        List<AnswerOptionRequest> options,
        List<AcceptedAnswerRequest> acceptedAnswers) {
    public QuestionRequest {
        options = options == null ? List.of() : options;
        acceptedAnswers = acceptedAnswers == null ? List.of() : acceptedAnswers;
    }
}