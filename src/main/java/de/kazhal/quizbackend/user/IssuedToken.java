package de.kazhal.quizbackend.user;

import java.time.Instant;

/**
 * Das Klartext-Token verlässt den Server genau einmal: in der Antwort auf den Login.
 */
public record IssuedToken(String token, Instant expiresAt) {
    @Override
    public String toString() {
        return "IssuedToken[expiresAt=" + expiresAt + "]";
    }
}