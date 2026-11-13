package de.kazhal.quizbackend;

import de.kazhal.quizbackend.common.QuizProperties;
import de.kazhal.quizbackend.question.Question;
import de.kazhal.quizbackend.question.evaluation.EvaluationResult;
import de.kazhal.quizbackend.question.evaluation.MultipleChoiceEvaluator;
import de.kazhal.quizbackend.question.evaluation.SubmittedAnswer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

class MultipleChoiceEvaluatorTest {
    private final Question question = TestData.multipleChoice();   // richtig: 20 und 22
    @Nested
    @DisplayName("Regel ALL_OR_NOTHING")
    class AllOrNothing {
        private final MultipleChoiceEvaluator evaluator =
                new MultipleChoiceEvaluator(TestData.quizProperties(QuizProperties.ScoringMode.ALL_OR_NOTHING));
        @Test
        @DisplayName("Genau die richtigen Optionen geben volle Punkte")
        void exactSelection() {
            EvaluationResult result = evaluator.evaluate(question, SubmittedAnswer.ofOptions(20L,
                    22L));
            assertThat(result.correct()).isTrue();
            assertThat(result.pointsAwarded()).isEqualTo(15);
        }
        @Test
        @DisplayName("Teilweise richtig gibt 0 Punkte")
        void partialSelection() {
            EvaluationResult result = evaluator.evaluate(question, SubmittedAnswer.ofOptions(20L));
            assertThat(result.correct()).isFalse();
            assertThat(result.pointsAwarded()).isZero();
        }
    }
    @Nested
    @DisplayName("Regel PARTIAL")
    class Partial {
        private final MultipleChoiceEvaluator evaluator =
                new MultipleChoiceEvaluator(TestData.quizProperties(QuizProperties.ScoringMode.PARTIAL));
        @ParameterizedTest(name = "Auswahl [{0}] gibt {1} Punkte")
        @CsvSource(textBlock = """
                20;22,    15
                20,       7
                20;22;21, 7
                20;21,    0
                21;23,    0
                '',       0
                """)
        void partialPoints(String selected, int expectedPoints) {
            Long[] ids = Arrays.stream(selected.split(";"))
                    .filter(s -> !s.isBlank())
                    .map(Long::valueOf)
                    .toArray(Long[]::new);
            EvaluationResult result = evaluator.evaluate(question, SubmittedAnswer.ofOptions(ids));
            assertThat(result.pointsAwarded()).isEqualTo(expectedPoints);
            assertThat(result.correct()).isEqualTo(expectedPoints == 15);
        }
    }
}