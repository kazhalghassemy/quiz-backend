package de.kazhal.quizbackend.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AcceptedAnswerRequest(
        @NotBlank(message = "Akzeptierte Antwort darf nicht leer sein")
        @Size(max = 500, message = "Akzeptierte Antwort darf höchstens 500 Zeichen haben")
        String text,
        boolean caseSensitive) {

}