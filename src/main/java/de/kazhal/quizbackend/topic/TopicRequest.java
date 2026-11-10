package de.kazhal.quizbackend.topic;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TopicRequest(
        @NotBlank(message = "Name darf nicht leer sein")
        @Size(max = 100, message = "Name darf höchstens 100 Zeichen haben")
        String name,
        @Size(max = 500, message = "Beschreibung darf höchstens 500 Zeichen haben")
        String description) {
}
