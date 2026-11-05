package de.kazhal.quizbackend.attempt;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class QuestionAlreadyAnsweredException extends RuntimeException {
    public QuestionAlreadyAnsweredException(Long questionId) {
        super("Frage " + questionId + " wurde in diesem Versuch schon beantwortet");
    }
}
