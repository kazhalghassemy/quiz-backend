package de.kazhal.quizbackend.attempt;

public class AttemptNotFoundException extends RuntimeException {

    public AttemptNotFoundException(Long id) {
        super("Versuch mit ID " + id + " nicht gefunden");
    }
}