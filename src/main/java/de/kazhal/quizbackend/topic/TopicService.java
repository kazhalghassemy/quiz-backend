package de.kazhal.quizbackend.topic;


import de.kazhal.quizbackend.question.Question;
import de.kazhal.quizbackend.question.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)

public class TopicService {
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;

    public TopicService(TopicRepository topicRepository, QuestionRepository questionRepository) {
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
    }

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