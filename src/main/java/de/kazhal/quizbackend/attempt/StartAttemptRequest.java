package de.kazhal.quizbackend.attempt;


import jakarta.validation.constraints.NotNull;

public record StartAttemptRequest(
        @NotNull(message = "topicId fehlt")
        Long topicId) {
}