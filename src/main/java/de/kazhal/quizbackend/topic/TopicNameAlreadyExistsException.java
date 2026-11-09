package de.kazhal.quizbackend.topic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TopicNameAlreadyExistsException extends RuntimeException {
    public TopicNameAlreadyExistsException(String name) {
        super("Ein Thema mit dem Namen '" + name + "' existiert bereits");
    }
}
