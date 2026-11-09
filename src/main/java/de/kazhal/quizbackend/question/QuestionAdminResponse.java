package de.kazhal.quizbackend.question;

import java.util.List;
/*
die Tabelle question in postgresql
alle Attribute sind gleich, außer die zwei Listen
 */
public record QuestionAdminResponse(
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

}
