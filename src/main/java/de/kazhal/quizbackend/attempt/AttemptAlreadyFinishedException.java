package de.kazhal.quizbackend.attempt;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AttemptAlreadyFinishedException extends RuntimeException {
    public AttemptAlreadyFinishedException(Long attemptId) {
        super("Versuch " + attemptId + " ist bereits abgeschlossen");
    }
}
