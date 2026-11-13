package de.kazhal.quizbackend;

import de.kazhal.quizbackend.question.evaluation.EvaluationResult;
import de.kazhal.quizbackend.question.evaluation.FreeTextEvaluator;
import de.kazhal.quizbackend.question.evaluation.SubmittedAnswer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class FreeTextEvaluatorTest {
    private final FreeTextEvaluator evaluator = new FreeTextEvaluator();

    @ParameterizedTest(name = "\"{0}\" wird akzeptiert")
    @ValueSource(strings = {"SELECT", "select", "Select", "  select  "})
    @DisplayName("Ohne Beachtung von Groß/Klein: Schreibweise und Leerzeichen am Rand egal")
    void ignoreCase(String answer) {
        EvaluationResult result = evaluator.evaluate(TestData.freeTextIgnoreCase(),
                SubmittedAnswer.ofText(answer));
        assertThat(result.correct()).isTrue();
        assertThat(result.pointsAwarded()).isEqualTo(10);
    }

    @Test
    @DisplayName("Mit Beachtung von Groß/Klein: exakte Schreibweise ist richtig")
    void caseSensitiveExact() {
        EvaluationResult result = evaluator.evaluate(TestData.freeTextCaseSensitive(),
                SubmittedAnswer.ofText("extends"));
        assertThat(result.correct()).isTrue();
    }

    @Test
    @DisplayName("Mit Beachtung von Groß/Klein: andere Schreibweise ist falsch")
    void caseSensitiveWrongCase() {
        EvaluationResult result = evaluator.evaluate(TestData.freeTextCaseSensitive(),
                SubmittedAnswer.ofText("Extends"));
        assertThat(result.correct()).isFalse();
    }

    @Test
    @DisplayName("Fehlende Antwort ist falsch und wirft keine Exception")
    void nullAnswer() {
        EvaluationResult result = evaluator.evaluate(TestData.freeTextIgnoreCase(),
                SubmittedAnswer.ofText(null));
        assertThat(result.correct()).isFalse();
    }
}