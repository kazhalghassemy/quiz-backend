package de.kazhal.quizbackend.topic;


import de.kazhal.quizbackend.common.error.ConflictException;

public class TopicNameAlreadyExistsException extends ConflictException {
    public TopicNameAlreadyExistsException(String name) {
       super(" TOPIC_NAME_TAKEN", "Thema mit ID " + name + " nicht gefunden");
    }
}
