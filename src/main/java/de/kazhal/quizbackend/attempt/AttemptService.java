package de.kazhal.quizbackend.attempt;


import de.kazhal.quizbackend.common.QuizProperties;
import de.kazhal.quizbackend.question.Question;
import de.kazhal.quizbackend.question.QuestionNotFoundException;
import de.kazhal.quizbackend.question.QuestionPlayResponse;
import de.kazhal.quizbackend.question.QuestionRepository;
import de.kazhal.quizbackend.question.evaluation.EvaluationResult;
import de.kazhal.quizbackend.question.evaluation.EvaluationService;
import de.kazhal.quizbackend.question.evaluation.SubmittedAnswer;
import de.kazhal.quizbackend.topic.Topic;
import de.kazhal.quizbackend.topic.TopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;


@Service
@Transactional(readOnly = true)
public class AttemptService {
    private static final Logger log = LoggerFactory.getLogger(AttemptService.class);
    private final TopicService topicService;
    private final QuestionRepository questionRepository;
    private final EvaluationService evaluationService;
    private final AttemptRepository attemptRepository;
    private final QuizProperties quizProperties;
    private final Clock clock;

    public AttemptService(TopicService topicService, QuestionRepository questionRepository,
                          EvaluationService evaluationService, AttemptRepository attemptRepository,
                          QuizProperties quizProperties, Clock clock) {
        this.topicService = topicService;
        this.questionRepository = questionRepository;
        this.evaluationService = evaluationService;
        this.attemptRepository = attemptRepository;
        this.quizProperties = quizProperties;
        this.clock = clock;
    }

    @Transactional
    public AttemptStartedResponse startAttempt(StartAttemptRequest request) {
        Topic topic = topicService.getTopic(request.topicId());
        List<Question> questions = topicService.getQuizQuestions(topic.getId());
        if (questions.isEmpty()) {
            throw new TopicHasNoQuestionsException(topic.getId());
        }
        int maxScore = questions.stream().mapToInt(Question::getPoints).sum();
        Attempt attempt = attemptRepository.save(new Attempt(topic, maxScore, Instant.now(clock)));
        log.debug("Versuch {} für Thema {} gestartet", attempt.getId(), topic.getId());
        return new AttemptStartedResponse(
                attempt.getId(),
                topic.getId(),
                topic.getName(),
                maxScore,
                attempt.getStartedAt(),
                questions.stream()
                        .map(q -> QuestionPlayResponse.from(q, quizProperties.defaultTimeLimit()))
                        .toList());
    }

    @Transactional
    public AnswerResultResponse submitAnswer(Long attemptId, AnswerRequest request) {
        Attempt attempt = getAttempt(attemptId);
        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new QuestionNotFoundException(request.questionId()));
        boolean sameTopic = question.getTopic().getId().equals(attempt.getTopic().getId());
        if (!sameTopic || !question.isActive()) {
            throw new QuestionNotInAttemptException(question.getId(), attemptId);
        }
        SubmittedAnswer submitted = new SubmittedAnswer(request.selectedOptionIds(),
                request.freeText());
        EvaluationResult result = evaluationService.evaluate(question, submitted);
        attempt.recordAnswer(question, submitted, result, Instant.now(clock));
        // kein save(): Dirty Checking (Punktestand) + Cascade (neue Antwort) beim Commit
        return AnswerResultResponse.of(question, result, attempt);
    }

    @Transactional
    public AttemptResultResponse finishAttempt(Long attemptId) {
        Attempt attempt = getAttempt(attemptId);
        attempt.finish(Instant.now(clock));
        return AttemptResultResponse.from(attempt);
    }

    public AttemptResultResponse findAttempt(Long attemptId) {
        return AttemptResultResponse.from(getAttempt(attemptId));
    }

    private Attempt getAttempt(Long attemptId) {
        return attemptRepository.findWithAnswersById(attemptId)
                .orElseThrow(() -> new AttemptNotFoundException(attemptId));
    }

    public List<AttemptSummaryResponse> findAttemptsOfTopic(Long topicId) {
        topicService.getTopic(topicId);   // 404, falls es das Thema nicht gibt
        return attemptRepository.findSummariesByTopicId(topicId);
    }
}