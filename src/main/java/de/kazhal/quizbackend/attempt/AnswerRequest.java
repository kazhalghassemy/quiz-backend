package de.kazhal.quizbackend.attempt;

import java.util.Set;
public record AnswerRequest(Long questionId, Set<Long> selectedOptionIds, String freeText) {}