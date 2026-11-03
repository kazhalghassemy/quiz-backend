package de.kazhal.quizbackend.topic;


import de.kazhal.quizbackend.attempt.AttemptRepository;
import de.kazhal.quizbackend.common.PageResponse;
import de.kazhal.quizbackend.question.Question;
import de.kazhal.quizbackend.question.QuestionRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TopicService {
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final AttemptRepository attemptRepository;

    public TopicService(TopicRepository topicRepository, QuestionRepository questionRepository,
                        AttemptRepository attemptRepository) {
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
    }

    // ---------- für die REST-API (liefern Response-Records) ----------
    public PageResponse<TopicResponse> findTopics(Pageable pageable) {
        return PageResponse.from(topicRepository.findAll(pageable).map(TopicResponse::from));
    }

    public TopicResponse findTopic(Long id) {
        return TopicResponse.from(getTopic(id));
    }

    @Transactional
    public TopicResponse createTopic(TopicRequest request) {
        if (topicRepository.existsByNameIgnoreCase(request.name())) {
            throw new TopicNameAlreadyExistsException(request.name());
        }
        Topic saved = topicRepository.save(new Topic(request.name(), request.description()));
        return TopicResponse.from(saved);
    }

    @Transactional
    public TopicResponse updateTopic(Long id, TopicRequest request) {
        Topic topic = getTopic(id);
        boolean nameChanged = !topic.getName().equalsIgnoreCase(request.name());
        if (nameChanged && topicRepository.existsByNameIgnoreCase(request.name())) {
            throw new TopicNameAlreadyExistsException(request.name());
        }
        topic.update(request.name(), request.description());   // kein save(): Dirty Checking
        return TopicResponse.from(topic);
    }

    @Transactional
    public void deleteTopic(Long id) {
        Topic topic = getTopic(id);
        if (attemptRepository.existsByTopicId(id)) {
            throw new TopicInUseException(id);
        }
        topicRepository.delete(topic);
    }

    // ---------- für andere Services (liefern Entities) ----------
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    public Topic getTopic(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new TopicNotFoundException(id));
    }

    public List<Question> getQuizQuestions(Long topicId) {
        if (!topicRepository.existsById(topicId)) {
            throw new TopicNotFoundException(topicId);
        }
        return questionRepository.findForQuiz(topicId);
    }
}