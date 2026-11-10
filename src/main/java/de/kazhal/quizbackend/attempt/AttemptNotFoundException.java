package de.kazhal.quizbackend.attempt;

import de.kazhal.quizbackend.common.error.NotFoundException;

public class AttemptNotFoundException extends NotFoundException {

    public AttemptNotFoundException(Long id) {
        super("ATTEMPT_NOT_FOUND", "Thema mit ID " + id + " nicht gefunden");
    }
}