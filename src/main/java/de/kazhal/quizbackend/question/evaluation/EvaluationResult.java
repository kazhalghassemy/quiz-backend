package de.kazhal.quizbackend.question.evaluation;

import de.kazhal.quizbackend.question.Question;

public record EvaluationResult(boolean correct, int pointsAwarded, int maxPoints, String explanation) {

    public static EvaluationResult of(boolean correct, Question q) {
        return new EvaluationResult(correct, correct ? q.getPoints() : 0, q.getPoints(), q.getExplanation());
    }
    public static EvaluationResult partial(int pointsAwarded, Question q) {
        return new EvaluationResult(pointsAwarded == q.getPoints(), pointsAwarded, q.getPoints(), q.getExplanation());
    }
}
