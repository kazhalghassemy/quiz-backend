package de.kazhal.quizbackend.attempt;


import de.kazhal.quizbackend.common.error.ConflictException;

public class TopicHasNoQuestionsException extends ConflictException {
    public TopicHasNoQuestionsException(Long topicId) {
        super(" TOPIC_HAS_NO_QUESTIONS", "Thema mit ID " + topicId + " nicht gefunden");
    }
}