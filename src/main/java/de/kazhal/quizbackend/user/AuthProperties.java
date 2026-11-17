package de.kazhal.quizbackend.user;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties(prefix = "quiz.auth")
public record AuthProperties(@DefaultValue("8h") Duration tokenValidity) {
}
