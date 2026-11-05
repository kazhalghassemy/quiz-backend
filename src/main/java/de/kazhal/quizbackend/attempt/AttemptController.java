package de.kazhal.quizbackend.attempt;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
@RestController
@RequestMapping("/api/attempts")
public class AttemptController {
    private final AttemptService attemptService;
    public AttemptController(AttemptService attemptService) {
        this.attemptService = attemptService;
    }
    @PostMapping
    public ResponseEntity<AttemptStartedResponse> startAttempt(@RequestBody StartAttemptRequest
                                                                       request) {
        AttemptStartedResponse started = attemptService.startAttempt(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(started.attemptId())
                .toUri();
        return ResponseEntity.created(location).body(started);
    }
    @PostMapping("/{id}/answers")
    public AnswerResultResponse submitAnswer(@PathVariable Long id, @RequestBody AnswerRequest
            request) {
        return attemptService.submitAnswer(id, request);
    }
    @PostMapping("/{id}/finish")
    public AttemptResultResponse finishAttempt(@PathVariable Long id) {
        return attemptService.finishAttempt(id);
    }
    @GetMapping("/{id}")
    public AttemptResultResponse getAttempt(@PathVariable Long id) {
        return attemptService.findAttempt(id);
    }
}