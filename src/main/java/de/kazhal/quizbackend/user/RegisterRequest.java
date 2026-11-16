package de.kazhal.quizbackend.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "E-Mail darf nicht leer sein")
        @Email(message = "Keine gültige E-Mail-Adresse")
        @Size(max = 255, message = "E-Mail darf höchstens 255 Zeichen haben")
        String email,
        @NotBlank(message = "Passwort darf nicht leer sein")
        @Size(min = 8, max = 72, message = "Passwort muss 8 bis 72 Zeichen haben")
        String password,
        @NotBlank(message = "Anzeigename darf nicht leer sein")
        @Size(max = 100, message = "Anzeigename darf höchstens 100 Zeichen haben")
        String displayName) {
    /**
     * Das Passwort darf nie in einem Log landen.
     */
    @Override
    public String toString() {
        return "RegisterRequest[email=" + email + ", displayName=" + displayName + "]";
    }
}
