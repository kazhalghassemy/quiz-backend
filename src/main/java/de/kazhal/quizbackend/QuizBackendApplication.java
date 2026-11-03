package de.kazhal.quizbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class  QuizBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizBackendApplication.class, args);
    }

}
