package de.kazhal.quizbackend.user;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "E-Mail darf nicht leer sein")
        String email,
        @NotBlank(message = "Passwort darf nicht leer sein")
        String password) {
    @Override
    public String toString() {
        return "LoginRequest[email=" + email + "]";
    }
}