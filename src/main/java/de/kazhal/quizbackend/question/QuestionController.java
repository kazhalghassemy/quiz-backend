package de.kazhal.quizbackend.question;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/topics/{topicId}/questions")
    public List<QuestionAdminResponse> getQuestions(@PathVariable Long topicId,
                                                    @RequestParam(required = false) Difficulty
                                                       difficulty) {
        return questionService.findQuestions(topicId, difficulty);
    }

    @GetMapping("/questions/{id}")
    public QuestionAdminResponse getQuestion(@PathVariable Long id) {
        return questionService.findQuestion(id);
    }

    @PostMapping("/topics/{topicId}/questions")
    public ResponseEntity<QuestionAdminResponse> createQuestion(@PathVariable Long topicId,
                                                                @RequestBody QuestionRequest request) {
        QuestionAdminResponse created = questionService.createQuestion(topicId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/questions/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
}