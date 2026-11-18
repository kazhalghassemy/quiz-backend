package de.kazhal.quizbackend.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@ConfigurationProperties(prefix = "quiz.cors")
public record CorsProperties(@DefaultValue("http://localhost:4200") List<String> allowedOrigins) {
}