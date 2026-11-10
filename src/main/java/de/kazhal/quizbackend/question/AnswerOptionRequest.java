package de.kazhal.quizbackend.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnswerOptionRequest(
        @NotBlank(message = "Text der Option darf nicht leer sein")
        @Size(max = 500, message = "Text der Option darf höchstens 500 Zeichen haben")
        String text,
        boolean correct) {
}