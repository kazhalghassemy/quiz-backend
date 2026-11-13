package de.kazhal.quizbackend;

import de.kazhal.quizbackend.common.QuizProperties;
import de.kazhal.quizbackend.question.evaluation.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluationServiceTest {
    private final QuizProperties properties = TestData.quizProperties(QuizProperties.ScoringMode.ALL_OR_NOTHING);

    private List<AnswerEvaluator> allEvaluators() {
        return List.of(new SingleChoiceEvaluator(), new MultipleChoiceEvaluator(properties),
                new TrueFalseEvaluator(), new FreeTextEvaluator());
    }

    @Test
    @DisplayName("Wählt den passenden Evaluator zum Fragetyp")
    void delegatesByType() {
        EvaluationService service = new EvaluationService(allEvaluators(), properties);
        assertThat(service.evaluate(TestData.trueFalse(),
                SubmittedAnswer.ofOptions(30L)).correct()).isTrue();
        assertThat(service.evaluate(TestData.freeTextIgnoreCase(),
                SubmittedAnswer.ofText("select")).correct()).isTrue();
    }

    @Test
    @DisplayName("Startet nicht, wenn für einen Fragetyp kein Evaluator existiert")
    void missingEvaluator() {
        assertThatThrownBy(() -> new EvaluationService(List.of(new SingleChoiceEvaluator()),
                properties))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Kein Evaluator für");
    }

    @Test
    @DisplayName("Startet nicht, wenn zwei Evaluatoren denselben Typ bedienen")
    void duplicateEvaluator() {
        List<AnswerEvaluator> evaluators = List.of(new SingleChoiceEvaluator(), new
                SingleChoiceEvaluator());
        assertThatThrownBy(() -> new EvaluationService(evaluators, properties))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Zwei Evaluatoren für");
    }

    @Test
    @DisplayName("Lehnt Fragen mit mehr als der erlaubten Punktzahl ab")
    void tooManyPoints() {
        QuizProperties strict = TestData.quizProperties(QuizProperties.ScoringMode.ALL_OR_NOTHING, 5);
        EvaluationService service = new EvaluationService(allEvaluators(), strict);
        assertThatThrownBy(() -> service.evaluate(TestData.singleChoice(),
                SubmittedAnswer.ofOptions(10L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maximal 5");
    }
}
