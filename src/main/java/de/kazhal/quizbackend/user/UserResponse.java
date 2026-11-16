package de.kazhal.quizbackend.user;

import java.time.Instant;
public record UserResponse(Long id, String email, String displayName, Role role, Instant createdAt)
{
    public static UserResponse from(AppUser user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getDisplayName(),
                user.getRole(), user.getCreatedAt());
    }
}