package de.kazhal.quizbackend.question;

import de.kazhal.quizbackend.topic.Topic;
import de.kazhal.quizbackend.topic.TopicService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
/*
@Service: Spring erzeugt davon eine Bean. Der QuestionController bekommt sie über seinen Konstruktor.
@Transactional(readOnly = true): Jede Methode läuft standardmäßig in einer lesenden Transaktion. Das ist wichtig,
weil QuestionResponse.from(...) die LAZY-Listen options und acceptedAnswers lädt, und das geht nur bei offener Transaktion.
 */
@Service
@Transactional(readOnly = true)
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final TopicService topicService;
    private final QuestionMapper questionMapper;

    public QuestionService(QuestionRepository questionRepository, TopicService topicService, QuestionMapper questionMapper) {
        this.questionRepository = questionRepository;
        this.topicService = topicService;
        this.questionMapper = questionMapper;
    }

    public List<QuestionAdminResponse> findQuestions(Long topicId, Difficulty difficulty) {
        topicService.getTopic(topicId);   // wirft 404, falls es das Thema nicht gibt
        List<Question> questions = difficulty == null
                ? questionRepository.findByTopicIdOrderByIdAsc(topicId)
                : questionRepository.findByTopicIdAndDifficulty(topicId, difficulty);
        return questions.stream().map(questionMapper::toAdminResponse).toList();
    }


    public QuestionAdminResponse findQuestion(Long id) {
        return questionMapper.toAdminResponse(questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException(id)));
    }


    @Transactional
    public QuestionAdminResponse createQuestion(Long topicId, QuestionRequest request) {
        Topic topic = topicService.getTopic(topicId);
        Question question = new Question(request.text(), request.type(), request.difficulty(),
                request.points(), request.timeLimitSeconds(), request.explanation());
        request.options().forEach(o -> question.addOption(o.text(), o.correct()));
        request.acceptedAnswers().forEach(a -> question.addAcceptedAnswer(a.text(),
                a.caseSensitive()));
        topic.addQuestion(question);
        Question saved = questionRepository.save(question);
        return questionMapper.toAdminResponse(saved);
    }
}
