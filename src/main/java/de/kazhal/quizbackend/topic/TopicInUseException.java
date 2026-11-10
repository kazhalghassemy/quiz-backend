package de.kazhal.quizbackend.topic;


import de.kazhal.quizbackend.common.error.ConflictException;

public class TopicInUseException extends ConflictException {
    public TopicInUseException(Long id) {
        super(" TOPIC_IN_USE", "Thema mit ID " + id + " nicht gefunden");
    }
}