package de.kazhal.quizbackend.question;

import java.util.List;
/*
die Tabelle question in postgresql
alle Attribute sind gleich, außer die zwei Listen
 */
public record QuestionResponse(
        Long id,
        Long topicId,
        String text,
        QuestionType type,
        Difficulty difficulty,
        int points,
        Integer timeLimitSeconds,
        String explanation,
        boolean active,
        List<AnswerOptionResponse> options,
        List<AcceptedAnswerResponse> acceptedAnswers) {
    public static QuestionResponse from(Question q) {
        return new QuestionResponse(
                q.getId(),
                q.getTopic().getId(),
                q.getText(),
                q.getType(),
                q.getDifficulty(),
                q.getPoints(),
                q.getTimeLimitSeconds(),
                q.getExplanation(),
                q.isActive(),
                q.getOptions().stream().map(AnswerOptionResponse::from).toList(),
                q.getAcceptedAnswers().stream().map(AcceptedAnswerResponse::from).toList());
    }
}