package de.kazhal.quizbackend.attempt;

import de.kazhal.quizbackend.user.QuizUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public List<AttemptSummaryResponse> getAttemptsOfTopic(@PathVariable Long topicId,
                                                           @AuthenticationPrincipal QuizUserDetails user) {  // ← NEU
        return attemptService.findAttemptsOfTopic(topicId, user);                                             // ← user NEU
    }
}