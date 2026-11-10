package de.kazhal.quizbackend.topic;


import de.kazhal.quizbackend.common.error.NotFoundException;

public class TopicNotFoundException extends NotFoundException {
    public TopicNotFoundException(Long id) {
        super("TOPIC_NOT_FOUND", "Thema mit ID " + id + " nicht gefunden");
    }
}

