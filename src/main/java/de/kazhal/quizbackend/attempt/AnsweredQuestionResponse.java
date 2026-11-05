package de.kazhal.quizbackend.attempt;


import de.kazhal.quizbackend.question.AnswerOption;

import java.util.List;
public record AnsweredQuestionResponse(
        Long questionId,
        String questionText,
        boolean correct,
        int pointsAwarded,
        int maxPoints,
        List<String> selectedOptions,
        String freeTextAnswer) {
    public static AnsweredQuestionResponse from(AttemptAnswer a) {
        return new AnsweredQuestionResponse(
                a.getQuestion().getId(),
                a.getQuestionTextSnapshot(),
                a.isCorrect(),
                a.getPointsAwarded(),
                a.getMaxPoints(),
                a.getSelectedOptions().stream().map(AnswerOption::getText).sorted().toList(),
                a.getFreeTextAnswer());
    }
}