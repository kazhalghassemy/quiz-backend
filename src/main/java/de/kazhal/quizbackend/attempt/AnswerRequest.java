package de.kazhal.quizbackend.attempt;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record AnswerRequest(
        @NotNull(message = "questionId fehlt")
        Long questionId,
        @Size(max = 10, message = "Höchstens 10 Optionen auswählbar")
        Set<Long> selectedOptionIds,
        @Size(max = 500, message = "Antwort darf höchstens 500 Zeichen haben")
        String freeText) {
}