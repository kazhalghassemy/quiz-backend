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
import de.kazhal.quizbackend.user.AppUser;
import de.kazhal.quizbackend.user.QuizUserDetails;
import de.kazhal.quizbackend.user.Role;
import de.kazhal.quizbackend.user.UserRepository;
import org.mapstruct.control.MappingControl;
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
    private final UserRepository userRepository;

    public AttemptService(TopicService topicService, QuestionRepository questionRepository,
                          EvaluationService evaluationService, AttemptRepository attemptRepository,
                          QuizProperties quizProperties, Clock clock, UserRepository userRepository) {
        this.topicService = topicService;
        this.questionRepository = questionRepository;
        this.evaluationService = evaluationService;
        this.attemptRepository = attemptRepository;
        this.quizProperties = quizProperties;
        this.clock = clock;
        this.userRepository = userRepository;
    }

    @Transactional
    public AttemptStartedResponse startAttempt(StartAttemptRequest request, QuizUserDetails currentUser) {  // NEU: currentUser
        Topic topic = topicService.getTopic(request.topicId());
        List<Question> questions = topicService.getQuizQuestions(topic.getId());
        if (questions.isEmpty()) {
            throw new TopicHasNoQuestionsException(topic.getId());
        }
        int maxScore = questions.stream().mapToInt(Question::getPoints).sum();
        AppUser user = userRepository.getReferenceById(currentUser.id());                         // NEU
        Attempt attempt = attemptRepository.save(new Attempt(user, topic, maxScore, Instant.now(clock)));  // NEU: user vorne
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

    public AnswerResultResponse submitAnswer(Long attemptId, AnswerRequest request, QuizUserDetails
            currentUser) {
        Attempt attempt = getOwnAttempt(attemptId, currentUser);
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
    public AttemptResultResponse finishAttempt(Long attemptId, QuizUserDetails currentUser) {
        Attempt attempt = getOwnAttempt(attemptId, currentUser);
        attempt.finish(Instant.now(clock));
        return AttemptResultResponse.from(attempt);
    }

    public AttemptResultResponse findAttempt(Long attemptId, QuizUserDetails currentUser) {
        Attempt attempt = getAttempt(attemptId);
        boolean admin = currentUser.role() == Role.ADMIN;
        if (!attempt.belongsTo(currentUser.id()) && !admin) {
            throw new AttemptNotFoundException(attemptId);   // 404 statt 403: verrät nicht, dass es ihn gibt
        }
        return AttemptResultResponse.from(attempt);
    }

    public List<AttemptSummaryResponse> findAttemptsOfTopic(Long topicId, QuizUserDetails currentUser) {
        topicService.getTopic(topicId);
        return attemptRepository.findSummariesByTopicIdAndUserId(topicId, currentUser.id());
    }

    private Attempt getAttempt(Long attemptId) {
        return attemptRepository.findWithAnswersById(attemptId)
                .orElseThrow(() -> new AttemptNotFoundException(attemptId));
    }

    /**
     * Antworten und Abschließen darf nur der Besitzer, auch kein Admin.
     */
    private Attempt getOwnAttempt(Long attemptId, QuizUserDetails currentUser) {
        Attempt attempt = getAttempt(attemptId);
        if (!attempt.belongsTo(currentUser.id())) {
            throw new AttemptNotFoundException(attemptId);
        }
        return attempt;
    }

}