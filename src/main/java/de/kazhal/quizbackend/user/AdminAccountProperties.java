package de.kazhal.quizbackend.user;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "quiz.admin")
public record AdminAccountProperties(String email, String password) {}
