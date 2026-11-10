package de.kazhal.quizbackend.question;

import de.kazhal.quizbackend.common.error.NotFoundException;


public class QuestionNotFoundException extends NotFoundException {
    public QuestionNotFoundException(Long id) {
        super(" QUESTION_NOT_FOUND\n", "Thema mit ID " + id + " nicht gefunden");
    }
}