package de.kazhal.quizbackend.topic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TopicNotFoundException extends RuntimeException {
    public TopicNotFoundException(Long id) {
        super("Thema mit ID " + id + " nicht gefunden");
    }
}

