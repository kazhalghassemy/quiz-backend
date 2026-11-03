package de.kazhal.quizbackend.attempt;

import de.kazhal.quizbackend.question.Question;
import de.kazhal.quizbackend.question.evaluation.EvaluationResult;
import de.kazhal.quizbackend.question.evaluation.EvaluationService;
import de.kazhal.quizbackend.question.evaluation.SubmittedAnswer;
import de.kazhal.quizbackend.topic.Topic;
import de.kazhal.quizbackend.topic.TopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class AttemptService {

    private static final Logger log = LoggerFactory.getLogger(AttemptService.class);

    private final TopicService topicService;
    private final EvaluationService evaluationService;
    private final AttemptRepository attemptRepository;

    public AttemptService(TopicService topicService, EvaluationService evaluationService,
                          AttemptRepository attemptRepository) {
        this.topicService = topicService;
        this.evaluationService = evaluationService;
        this.attemptRepository = attemptRepository;
    }

    /** Spielt ein ganzes Quiz in einem Rutsch und speichert den Versuch. */
    @Transactional
    public Long playWholeQuiz(Long topicId, Map<Long, SubmittedAnswer> answers) {
        Topic topic = topicService.getTopic(topicId);
        List<Question> questions = topicService.getQuizQuestions(topicId);
        int maxScore = questions.stream().mapToInt(Question::getPoints).sum();

        Attempt attempt = new Attempt(topic, maxScore, Instant.now());
        for (Question question : questions) {
            SubmittedAnswer answer = answers.getOrDefault(question.getId(), SubmittedAnswer.ofOptions());
            EvaluationResult result = evaluationService.evaluate(question, answer);
            attempt.recordAnswer(question, answer, result, Instant.now());
        }
        attempt.finish(Instant.now());

        Attempt saved = attemptRepository.save(attempt);
        log.debug("Versuch {} für Thema {} gespeichert: {}/{}",
                saved.getId(), topicId, saved.getTotalScore(), saved.getMaxScore());
        return saved.getId();
    }

    @Transactional(readOnly = true)
    public Attempt getAttemptWithAnswers(Long attemptId) {
        return attemptRepository.findWithAnswersById(attemptId)
                .orElseThrow(() -> new AttemptNotFoundException(attemptId));
    }
}