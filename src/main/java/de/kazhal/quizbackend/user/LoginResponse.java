package de.kazhal.quizbackend.user;

import java.time.Instant;

public record LoginResponse(String token, String tokenType, Instant expiresAt, UserResponse user) {
}
