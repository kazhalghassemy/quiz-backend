package de.kazhal.quizbackend.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InfoController {

    public record AppInfo(String name, List<String> activeProfiles,
                          String multipleChoiceScoring, long defaultTimeLimitSeconds) {}

    private final String appName;
    private final Environment environment;
    private final QuizProperties quizProperties;

    public InfoController(@Value("${spring.application.name}") String appName,
                          Environment environment,
                          QuizProperties quizProperties) {
        this.appName = appName;
        this.environment = environment;
        this.quizProperties = quizProperties;
    }

    @GetMapping("/info")
    public AppInfo info() {
        return new AppInfo(appName,
                List.of(environment.getActiveProfiles()),
                quizProperties.multipleChoice().scoring().name(),
                quizProperties.defaultTimeLimit().toSeconds());
    }
}