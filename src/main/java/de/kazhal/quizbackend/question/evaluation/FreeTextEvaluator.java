package de.kazhal.quizbackend.question.evaluation;

import de.kazhal.quizbackend.question.Question;
import de.kazhal.quizbackend.question.QuestionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FreeTextEvaluator implements AnswerEvaluator {

    private static final Logger log = LoggerFactory.getLogger(FreeTextEvaluator.class);
    @Override
    public QuestionType supports() { return QuestionType.FREE_TEXT; }

    @Override
    public EvaluationResult evaluate(Question q, SubmittedAnswer a) {
        String given = a.freeText() == null ? "" : a.freeText().trim();
        boolean correct = q.getAcceptedAnswers().stream().anyMatch(acc -> {
            log.trace("Vergleiche '{}' mit '{}' (caseSensitive={})", given, acc.getText(), acc.isCaseSensitive());
            return acc.isCaseSensitive()
                    ? acc.getText().trim().equals(given)
                    : acc.getText().trim().equalsIgnoreCase(given);
        });
        return EvaluationResult.of(correct, q);
    }
}



// in evaluate():
