package de.kazhal.quizbackend.attempt;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TopicAttemptController {
    private final AttemptService attemptService;

    public TopicAttemptController(AttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @GetMapping("/api/topics/{topicId}/attempts")
    public List<AttemptSummaryResponse> getAttemptsOfTopic(@PathVariable Long topicId) {
        return attemptService.findAttemptsOfTopic(topicId);
    }
}