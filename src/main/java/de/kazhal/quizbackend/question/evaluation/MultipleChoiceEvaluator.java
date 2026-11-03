package de.kazhal.quizbackend.question.evaluation;

import de.kazhal.quizbackend.common.QuizProperties;
import de.kazhal.quizbackend.question.Question;
import de.kazhal.quizbackend.question.QuestionType;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public class MultipleChoiceEvaluator implements AnswerEvaluator {

    private final QuizProperties properties;

    public MultipleChoiceEvaluator(QuizProperties properties) {
        this.properties = properties;
    }

    @Override
    public QuestionType supports() { return QuestionType.MULTIPLE_CHOICE; }

    @Override
    public EvaluationResult evaluate(Question q, SubmittedAnswer a) {
        Set<Long> correctIds = q.correctOptionIds();

        if (properties.multipleChoice().scoring() == QuizProperties.ScoringMode.ALL_OR_NOTHING) {
            return EvaluationResult.of(correctIds.equals(a.selectedOptionIds()), q);
        }

        // PARTIAL: richtige Treffer minus falsch gewählte, nie unter 0, abgerundet
        long hits = a.selectedOptionIds().stream().filter(correctIds::contains).count();
        long wrong = a.selectedOptionIds().size() - hits;
        long net = Math.max(0, hits - wrong);
        int awarded = (int) (q.getPoints() * net / correctIds.size());
        return EvaluationResult.partial(awarded, q);
    }
}