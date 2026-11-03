package de.kazhal.quizbackend.question;

import de.kazhal.quizbackend.topic.Topic;
import de.kazhal.quizbackend.topic.TopicService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
@Transactional(readOnly = true)
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final TopicService topicService;
    public QuestionService(QuestionRepository questionRepository, TopicService topicService) {
        this.questionRepository = questionRepository;
        this.topicService = topicService;
    }
    public List<QuestionResponse> findQuestions(Long topicId, Difficulty difficulty) {
        topicService.getTopic(topicId);   // wirft 404, falls es das Thema nicht gibt
        List<Question> questions = difficulty == null
                ? questionRepository.findByTopicIdOrderByIdAsc(topicId)
                : questionRepository.findByTopicIdAndDifficulty(topicId, difficulty);
        return questions.stream().map(QuestionResponse::from).toList();
    }
    public QuestionResponse findQuestion(Long id) {
        return QuestionResponse.from(questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException(id)));
    }
    @Transactional
    public QuestionResponse createQuestion(Long topicId, QuestionRequest request) {
        Topic topic = topicService.getTopic(topicId);
        Question question = new Question(request.text(), request.type(), request.difficulty(),
                request.points(), request.timeLimitSeconds(), request.explanation());
        request.options().forEach(o -> question.addOption(o.text(), o.correct()));
        request.acceptedAnswers().forEach(a -> question.addAcceptedAnswer(a.text(),
                a.caseSensitive()));
        topic.addQuestion(question);
        Question saved = questionRepository.save(question);
        return QuestionResponse.from(saved);
    }
}
