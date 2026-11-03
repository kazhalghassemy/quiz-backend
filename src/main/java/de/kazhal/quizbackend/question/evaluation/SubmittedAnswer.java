package de.kazhal.quizbackend.question.evaluation;

import java.util.Set;

public record SubmittedAnswer(Set<Long> selectedOptionIds, String freeText) {

    public SubmittedAnswer {
        selectedOptionIds = selectedOptionIds == null ? Set.of() : Set.copyOf(selectedOptionIds);
    }

    public static SubmittedAnswer ofOptions(Long... ids) {
        return new SubmittedAnswer(Set.of(ids), null);
    }

    public static SubmittedAnswer ofText(String text) {
        return new SubmittedAnswer(Set.of(), text);
    }
}
