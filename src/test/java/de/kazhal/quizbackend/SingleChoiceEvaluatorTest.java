package de.kazhal.quizbackend;


import de.kazhal.quizbackend.question.Question;
import de.kazhal.quizbackend.question.evaluation.EvaluationResult;
import de.kazhal.quizbackend.question.evaluation.SingleChoiceEvaluator;
import de.kazhal.quizbackend.question.evaluation.SubmittedAnswer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SingleChoiceEvaluatorTest {
    private final SingleChoiceEvaluator evaluator = new SingleChoiceEvaluator();
    private final Question question = TestData.singleChoice();
    @Test
    @DisplayName("Richtige Option gibt volle Punkte")
    void correctOption() {
        EvaluationResult result = evaluator.evaluate(question, SubmittedAnswer.ofOptions(10L));
        assertThat(result.correct()).isTrue();
        assertThat(result.pointsAwarded()).isEqualTo(10);
        assertThat(result.maxPoints()).isEqualTo(10);
    }
    @Test
    @DisplayName("Falsche Option gibt 0 Punkte und liefert trotzdem die Erklärung")
    void wrongOption() {
        EvaluationResult result = evaluator.evaluate(question, SubmittedAnswer.ofOptions(11L));
        assertThat(result.correct()).isFalse();
        assertThat(result.pointsAwarded()).isZero();
        assertThat(result.explanation()).contains("final");
    }
    @Test
    @DisplayName("Zwei Optionen gewählt ist falsch, auch wenn die richtige dabei ist")
    void twoOptionsSelected() {
        EvaluationResult result = evaluator.evaluate(question, SubmittedAnswer.ofOptions(10L, 11L));
        assertThat(result.correct()).isFalse();
    }
    @Test
    @DisplayName("Keine Auswahl ist falsch")
    void nothingSelected() {
        EvaluationResult result = evaluator.evaluate(question, SubmittedAnswer.ofOptions());
        assertThat(result.correct()).isFalse();
    }
}
