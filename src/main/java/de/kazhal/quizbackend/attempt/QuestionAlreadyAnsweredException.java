package de.kazhal.quizbackend.attempt;

import de.kazhal.quizbackend.common.error.ConflictException;
public class QuestionAlreadyAnsweredException extends ConflictException {
    public QuestionAlreadyAnsweredException(Long questionId) {
        super("QUESTION_ALREADY_ANSWERED", "Thema mit ID " + questionId + " nicht gefunden");
    }
}

