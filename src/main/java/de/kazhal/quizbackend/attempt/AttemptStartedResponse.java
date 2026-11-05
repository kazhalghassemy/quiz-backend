package de.kazhal.quizbackend.attempt;


import de.kazhal.quizbackend.question.QuestionPlayResponse;

import java.time.Instant;
import java.util.List;
public record AttemptStartedResponse(
        Long attemptId,
        Long topicId,
        String topicName,
        int maxScore,
        Instant startedAt,
        List<QuestionPlayResponse> questions) {}