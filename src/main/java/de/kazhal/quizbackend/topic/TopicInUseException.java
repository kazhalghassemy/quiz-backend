package de.kazhal.quizbackend.topic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TopicInUseException extends RuntimeException {
    public TopicInUseException(Long id) {
        super("Thema " + id + " hat bereits Quiz-Versuche und kann nicht gelöscht werden");
    }
}