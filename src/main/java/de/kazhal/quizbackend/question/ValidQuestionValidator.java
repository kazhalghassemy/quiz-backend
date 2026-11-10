package de.kazhal.quizbackend.question;


import de.kazhal.quizbackend.common.QuizProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidQuestionValidator implements ConstraintValidator<ValidQuestion, QuestionRequest> {
    private final QuizProperties quizProperties;

    public ValidQuestionValidator(QuizProperties quizProperties) {
        this.quizProperties = quizProperties;
    }

    @Override
    public boolean isValid(QuestionRequest q, ConstraintValidatorContext ctx) {
        if (q == null || q.type() == null) {
            return true;   // fehlender Typ wird schon von @NotNull gemeldet
        }
        ctx.disableDefaultConstraintViolation();
        int optionCount = q.options().size();
        long correctCount = q.options().stream().filter(AnswerOptionRequest::correct).count();
        boolean valid = true;
        switch (q.type()) {
            case SINGLE_CHOICE -> {
                valid &= check(optionCount >= 2, ctx, "options", "Single Choice braucht mindestens 2 Optionen");
                valid &= check(correctCount == 1, ctx, "options", "Single Choice braucht genau 1 richtige Option");
                valid &= check(q.acceptedAnswers().isEmpty(), ctx, "acceptedAnswers",
                        "Nur Freitext-Fragen haben akzeptierte Antworten");
            }
            case MULTIPLE_CHOICE -> {
                valid &= check(optionCount >= 2, ctx, "options", "Multiple Choice braucht mindestens 2 Optionen");
                valid &= check(correctCount >= 1, ctx, "options", "Multiple Choice braucht mindestens 1 richtige Option");
                valid &= check(q.acceptedAnswers().isEmpty(), ctx, "acceptedAnswers",
                        "Nur Freitext-Fragen haben akzeptierte Antworten");
            }
            case TRUE_FALSE -> {
                valid &= check(optionCount == 2, ctx, "options", "Wahr/Falsch braucht genau 2 Optionen");
                valid &= check(correctCount == 1, ctx, "options", "Wahr/Falsch braucht genau 1 richtige Option");
                valid &= check(q.acceptedAnswers().isEmpty(), ctx, "acceptedAnswers",
                        "Nur Freitext-Fragen haben akzeptierte Antworten");
            }
            case FREE_TEXT -> {
                valid &= check(!q.acceptedAnswers().isEmpty(), ctx, "acceptedAnswers",
                        "Freitext braucht mindestens 1 akzeptierte Antwort");
                valid &= check(optionCount == 0, ctx, "options", "Freitext-Fragen haben keine Antwortoptionen");
            }
        }
        int maxPoints = quizProperties.maxPointsPerQuestion();
        valid &= check(q.points() <= maxPoints, ctx, "points", "Höchstens " + maxPoints + " Punkte erlaubt");
        return valid;
    }

    private static boolean check(boolean condition, ConstraintValidatorContext ctx,
                                 String field, String message) {
        if (!condition) {
            ctx.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(field)
                    .addConstraintViolation();
        }
        return condition;
    }
}