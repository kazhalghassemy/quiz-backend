package de.kazhal.quizbackend.attempt;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TopicHasNoQuestionsException extends RuntimeException {
    public TopicHasNoQuestionsException(Long topicId) {
        super("Thema " + topicId + " hat keine aktiven Fragen");
    }
}