package de.kazhal.quizbackend.topic;


import de.kazhal.quizbackend.common.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/topics")
public class TopicController {
    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    public PageResponse<TopicResponse> getTopics(@PageableDefault(size = 20, sort = "name") Pageable
                                                         pageable) {
        return topicService.findTopics(pageable);
    }

    @GetMapping("/{id}")
    public TopicResponse getTopic(@PathVariable Long id) {
        return topicService.findTopic(id);
    }

    @PostMapping
    public ResponseEntity<TopicResponse> createTopic(@Valid @RequestBody TopicRequest request) {
        TopicResponse created = topicService.createTopic(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public TopicResponse updateTopic(@PathVariable Long id, @Valid @RequestBody TopicRequest request) {
        return topicService.updateTopic(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
    }


}