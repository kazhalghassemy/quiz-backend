package de.kazhal.quizbackend.attempt;

import de.kazhal.quizbackend.user.QuizUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public AttemptStartedResponse startAttempt(@Valid @RequestBody StartAttemptRequest request,
                                               @AuthenticationPrincipal QuizUserDetails user) {   // ← NEU
        return attemptService.startAttempt(request, user);                                          // ← user NEU
    }


    @PostMapping("/{id}/answers")
    public AnswerResultResponse submitAnswer(@PathVariable Long id,
                                             @Valid @RequestBody AnswerRequest request,
                                             @AuthenticationPrincipal QuizUserDetails user) {      // ← NEU
        return attemptService.submitAnswer(id, request, user);                                      // ← user NEU
    }

    @PostMapping("/{id}/finish")
    public AttemptResultResponse finishAttempt(@PathVariable Long id,
                                               @AuthenticationPrincipal QuizUserDetails user) {    // ← NEU
        return attemptService.finishAttempt(id, user);                                              // ← user NEU
    }

    @GetMapping("/{id}")
    public AttemptResultResponse getAttempt(@PathVariable Long id,
                                            @AuthenticationPrincipal QuizUserDetails user) {       // ← NEU
        return attemptService.findAttempt(id, user);                                                // ← user NEU
    }
}