package de.kazhal.quizbackend.question;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class QuestionNotFoundException extends RuntimeException {
    public QuestionNotFoundException(Long id) {
        super("Frage mit ID " + id + " nicht gefunden");
    }
}