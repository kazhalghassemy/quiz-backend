package de.kazhal.quizbackend.common;

import de.kazhal.quizbackend.question.evaluation.EvaluationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "quiz.demo", name = "enabled", havingValue = "true")
public class DemoConfig {
    private static final Logger log = LoggerFactory.getLogger(DemoConfig.class);
    @Bean
    CommandLineRunner evaluationDemo(EvaluationService service) {
        return args -> {
            log.info("3) CommandLineRunner: Demo startet");
            // ... die vier Fragen und sechs log.info-Zeilen von Tag 2 bleiben unverändert
        };
    }
}