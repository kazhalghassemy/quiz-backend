package de.kazhal.quizbackend.attempt;

import de.kazhal.quizbackend.common.error.BadRequestException;


public class QuestionNotInAttemptException extends BadRequestException {
    public QuestionNotInAttemptException(Long questionId, Long attemptId) {
        super("QUESTION_NOT_IN_ATTEMPT", "Thema mit ID " + questionId + " nicht gefunden");
    }
}



