package de.kazhal.quizbackend.attempt;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class QuestionNotInAttemptException extends RuntimeException {
    public QuestionNotInAttemptException(Long questionId, Long attemptId) {
        super("Frage " + questionId + " gehört nicht zu Versuch " + attemptId);
    }
}