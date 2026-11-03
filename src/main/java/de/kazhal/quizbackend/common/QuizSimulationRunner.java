package de.kazhal.quizbackend.common;



import de.kazhal.quizbackend.attempt.Attempt;
import de.kazhal.quizbackend.attempt.AttemptAnswer;
import de.kazhal.quizbackend.attempt.AttemptService;
import de.kazhal.quizbackend.question.AnswerOption;
import de.kazhal.quizbackend.question.evaluation.SubmittedAnswer;
import de.kazhal.quizbackend.topic.Topic;
import de.kazhal.quizbackend.topic.TopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConditionalOnProperty(prefix = "quiz.demo", name = "enabled", havingValue = "true")
public class QuizSimulationRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(QuizSimulationRunner.class);

    private static final Map<Long, SubmittedAnswer> SAMPLE_ANSWERS = Map.of(
            1L, SubmittedAnswer.ofOptions(1L),
            2L, SubmittedAnswer.ofOptions(10L),
            3L, SubmittedAnswer.ofText("Extends"),
            4L, SubmittedAnswer.ofText("select"),
            5L, SubmittedAnswer.ofOptions(30L, 31L, 32L),
            6L, SubmittedAnswer.ofOptions(41L));

    private final TopicService topicService;
    private final AttemptService attemptService;

    public QuizSimulationRunner(TopicService topicService, AttemptService attemptService) {
        this.topicService = topicService;
        this.attemptService = attemptService;
    }

    @Override   // bewusst OHNE @Transactional: die Services bringen ihre eigenen Transaktionen mit
    public void run(String... args) {
        for (Topic topic : topicService.getAllTopics()) {
            Long attemptId = attemptService.playWholeQuiz(topic.getId(), SAMPLE_ANSWERS);
            printAttempt(attemptService.getAttemptWithAnswers(attemptId));
        }
    }

    private void printAttempt(Attempt attempt) {
        log.info("=== Versuch {} – Thema: {} ===", attempt.getId(), attempt.getTopic().getName());
        for (AttemptAnswer answer : attempt.getAnswers()) {
            log.info("{} {} -> {}/{} | Antwort: {}", status(answer),
                    answer.getQuestionTextSnapshot(),
                    answer.getPointsAwarded(), answer.getMaxPoints(), describe(answer));
        }
        log.info("Ergebnis: {} von {} Punkten ({} %)",
                attempt.getTotalScore(), attempt.getMaxScore(), attempt.percent());
    }

    private static String describe(AttemptAnswer answer) {
        if (answer.getFreeTextAnswer() != null) {
            return "\"" + answer.getFreeTextAnswer() + "\"";
        }
        return answer.getSelectedOptions().stream()
                .map(AnswerOption::getText).sorted().toList().toString();
    }

    private static String status(AttemptAnswer answer) {
        if (answer.isCorrect()) {
            return "[RICHTIG]   ";
        }
        return answer.getPointsAwarded() > 0 ? "[TEILWEISE]" : "[FALSCH]    ";
    }
}