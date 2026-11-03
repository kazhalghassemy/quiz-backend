package de.kazhal.quizbackend.topic;

public record TopicResponse(Long id, String name, String description) {
    public static TopicResponse from(Topic topic) {
        return new TopicResponse(topic.getId(), topic.getName(), topic.getDescription());
    }
}
/*
Die Grundidee: zwei Richtungen

Client  ── JSON ──►  TopicRequest   ──►  Service  ──►  Topic (Entity)  ──►  DB
Client  ◄── JSON ──  TopicResponse  ◄──  Service  ◄──  Topic (Entity)  ◄──  DB

TopicRequest ist das, was reinkommt (POST, PUT).
TopicResponse ist das, was rausgeht (GET, und die Antwort auf POST und PUT).

Die Entity Topic selbst verlässt den Server nie.
 */