package de.kazhal.quizbackend.question.evaluation;

import de.kazhal.quizbackend.common.QuizProperties;   // NEU
import de.kazhal.quizbackend.question.Question;
import de.kazhal.quizbackend.question.QuestionType;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;                               // NEU
import org.slf4j.LoggerFactory;                        // NEU
import org.springframework.stereotype.Service;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class EvaluationService {

    private static final Logger log = LoggerFactory.getLogger(EvaluationService.class);   // NEU

    private final Map<QuestionType, AnswerEvaluator> evaluators = new EnumMap<>(QuestionType.class);
    private final QuizProperties properties;                                              // NEU

    public EvaluationService(List<AnswerEvaluator> evaluatorList, QuizProperties properties) {
        this.properties = properties;
        for (AnswerEvaluator evaluator : evaluatorList) {
            AnswerEvaluator previous = evaluators.put(evaluator.supports(), evaluator);
            if (previous != null) {
                throw new IllegalStateException("Zwei Evaluatoren für " + evaluator.supports() + ": "
                        + previous.getClass().getSimpleName() + " und " + evaluator.getClass().getSimpleName());
            }
        }
        for (QuestionType type : QuestionType.values()) {
            if (!evaluators.containsKey(type)) {
                throw new IllegalStateException("Kein Evaluator für Fragetyp " + type);
            }
        }
    }

    @PostConstruct
    void init() {
        log.info("Bereit mit {} Evaluatoren {}", evaluators.size(), evaluators.keySet());
    }

    @PreDestroy
    void shutdown() {
        log.info("@PreDestroy: EvaluationService wird beendet");
    }

    public EvaluationResult evaluate(Question question, SubmittedAnswer answer) {
        if (question.getPoints() > properties.maxPointsPerQuestion()) {
            throw new IllegalArgumentException("Frage " + question.getId() + " hat " + question.getPoints()
                    + " Punkte, erlaubt sind maximal " + properties.maxPointsPerQuestion());
        }
        EvaluationResult result = evaluators.get(question.getType()).evaluate(question, answer);
        log.debug("Frage {} ({}): {} von {} Punkten", question.getId(), question.getType(),
                result.pointsAwarded(), result.maxPoints());
        return result;
    }

}