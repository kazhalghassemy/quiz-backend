package de.kazhal.quizbackend.common;


import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "quiz")
public record QuizProperties(
        @DefaultValue("30s") Duration defaultTimeLimit,
        @DefaultValue("100") @Positive int maxPointsPerQuestion,
        @DefaultValue MultipleChoice multipleChoice) {

    public record MultipleChoice(@DefaultValue("ALL_OR_NOTHING") ScoringMode scoring) {}

    public enum ScoringMode { ALL_OR_NOTHING, PARTIAL }

}
