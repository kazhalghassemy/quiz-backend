package de.kazhal.quizbackend.topic;

public class TopicNotFoundException extends RuntimeException {

    public TopicNotFoundException(Long id) {
        super("Thema mit ID " + id + " nicht gefunden");
    }
}