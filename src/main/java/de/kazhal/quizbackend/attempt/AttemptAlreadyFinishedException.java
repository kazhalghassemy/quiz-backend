package de.kazhal.quizbackend.attempt;


import de.kazhal.quizbackend.common.error.ConflictException;

public class AttemptAlreadyFinishedException extends ConflictException {
    public AttemptAlreadyFinishedException(Long attemptId) {
        super("ATTEMPT_ALREADY_FINISHED", "Versuch " + attemptId + " ist bereits abgeschlossen");
    }
}