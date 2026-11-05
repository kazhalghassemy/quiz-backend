package de.kazhal.quizbackend.attempt;


import de.kazhal.quizbackend.question.AcceptedAnswer;
import de.kazhal.quizbackend.question.Question;
import de.kazhal.quizbackend.question.evaluation.EvaluationResult;

import java.util.List;
public record AnswerResultResponse(
        Long questionId,
        boolean correct,
        int pointsAwarded,
        int maxPoints,
        String explanation,
        List<Long> correctOptionIds,
        List<String> acceptedAnswers,
        int totalScore,
        int answeredCount) {
    public static AnswerResultResponse of(Question question, EvaluationResult result, Attempt
            attempt) {
        return new AnswerResultResponse(
                question.getId(),
                result.correct(),
                result.pointsAwarded(),
                result.maxPoints(),
                result.explanation(),
                question.correctOptionIds().stream().sorted().toList(),
                question.getAcceptedAnswers().stream().map(AcceptedAnswer::getText).toList(),
                attempt.getTotalScore(),
                attempt.getAnswers().size());
    }
}