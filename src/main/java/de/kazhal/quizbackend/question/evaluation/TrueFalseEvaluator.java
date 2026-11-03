package de.kazhal.quizbackend.question.evaluation;

import de.kazhal.quizbackend.question.Question;
import de.kazhal.quizbackend.question.QuestionType;
import org.springframework.stereotype.Component;

@Component
public class TrueFalseEvaluator implements AnswerEvaluator {

    @Override
    public QuestionType supports() { return QuestionType.TRUE_FALSE; }

    @Override
    public EvaluationResult evaluate(Question q, SubmittedAnswer a) {
        // Zwei Optionen ("Wahr"/"Falsch"), genau eine ist richtig
        boolean correct = a.selectedOptionIds().size() == 1
                && q.correctOptionIds().containsAll(a.selectedOptionIds());
        return EvaluationResult.of(correct, q);
    }
}