package de.kazhal.quizbackend;

import de.kazhal.quizbackend.common.QuizProperties;
import de.kazhal.quizbackend.question.*;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ValidQuestionValidatorTest {
    private final ValidQuestionValidator validator =
            new ValidQuestionValidator(TestData.quizProperties(QuizProperties.ScoringMode.ALL_OR_NOTHING));
    private final ConstraintValidatorContext ctx = mock(ConstraintValidatorContext.class,
            RETURNS_DEEP_STUBS);

    private static QuestionRequest request(QuestionType type, int points,
                                           List<AnswerOptionRequest> options,
                                           List<AcceptedAnswerRequest> accepted) {
        return new QuestionRequest("Testfrage", type, Difficulty.EASY, points, null, null, options,
                accepted);
    }

    @Test
    @DisplayName("Gültige Single-Choice-Frage")
    void validSingleChoice() {
        QuestionRequest q = request(QuestionType.SINGLE_CHOICE, 10,
                List.of(new AnswerOptionRequest("A", true), new AnswerOptionRequest("B", false)),
                List.of());
        assertThat(validator.isValid(q, ctx)).isTrue();
        verify(ctx, never()).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    @DisplayName("Single Choice mit zwei richtigen Optionen")
    void singleChoiceTwoCorrect() {
        QuestionRequest q = request(QuestionType.SINGLE_CHOICE, 10,
                List.of(new AnswerOptionRequest("A", true), new AnswerOptionRequest("B", true)),
                List.of());
        assertThat(validator.isValid(q, ctx)).isFalse();
        verify(ctx).buildConstraintViolationWithTemplate("Single Choice braucht genau 1 richtige Option");
    }

    @Test
    @DisplayName("Freitext mit Option und ohne akzeptierte Antwort meldet beide Fehler")
    void freeTextTwoErrors() {
        QuestionRequest q = request(QuestionType.FREE_TEXT, 10,
                List.of(new AnswerOptionRequest("main", true)), List.of());
        assertThat(validator.isValid(q, ctx)).isFalse();
        verify(ctx).buildConstraintViolationWithTemplate("Freitext braucht mindestens 1 akzeptierte Antwort");
        verify(ctx).buildConstraintViolationWithTemplate("Freitext-Fragen haben keine Antwortoptionen");
    }

    @Test
    @DisplayName("Wahr/Falsch mit drei Optionen")
    void trueFalseThreeOptions() {
        QuestionRequest q = request(QuestionType.TRUE_FALSE, 5,
                List.of(new AnswerOptionRequest("Wahr", true), new AnswerOptionRequest("Falsch",
                                false),
                        new AnswerOptionRequest("Vielleicht", false)), List.of());
        assertThat(validator.isValid(q, ctx)).isFalse();
        verify(ctx).buildConstraintViolationWithTemplate("Wahr/Falsch braucht genau 2 Optionen");
    }

    @Test
    @DisplayName("Mehr Punkte als in QuizProperties erlaubt")
    void tooManyPoints() {
        QuestionRequest q = request(QuestionType.FREE_TEXT, 500,
                List.of(), List.of(new AcceptedAnswerRequest("main", true)));
        assertThat(validator.isValid(q, ctx)).isFalse();
        verify(ctx).buildConstraintViolationWithTemplate("Höchstens 100 Punkte erlaubt");
    }

    @Test
    @DisplayName("Fehlender Typ wird dem @NotNull überlassen")
    void missingType() {
        QuestionRequest q = request(null, 10, List.of(), List.of());
        assertThat(validator.isValid(q, ctx)).isTrue();
    }
}