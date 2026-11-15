package de.kazhal.quizbackend;

import com.jayway.jsonpath.JsonPath;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
// @AutoConfigureMockMvc: Import mit Alt+Enter, das Package hängt von der Boot-Version ab
import static org.hamcrest.Matchers.contains;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Diese Klasse ist die Probefahrt mit dem ganzen Auto.
 * Sie spielt dein Quiz so durch, wie es ein Angular-Frontend später tun würde, nur ohne Browser.
 */
@SpringBootTest(properties = "quiz.multiple-choice.scoring=PARTIAL")
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class QuizFlowIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long startAttempt(long topicId) throws Exception {
        String json = mockMvc.perform(post("/api/attempts")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "topicId": %d }
                                """.formatted(topicId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.parse(json).read("$.attemptId", Long.class);
    }

    private ResultActions answer(Long attemptId, String body) throws Exception {
        return mockMvc.perform(post("/api/attempts/{id}/answers", attemptId)
                .contentType(APPLICATION_JSON)
                .content(body));
    }

    @Test
    @DisplayName("Ein komplettes Quiz: starten, antworten, abschließen")
    void fullQuiz() throws Exception {
        Long attemptId = startAttempt(2L);
        answer(attemptId, """
                { "questionId": 4, "freeText": "select" }
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(true))
                .andExpect(jsonPath("$.pointsAwarded").value(10));
        answer(attemptId, """
                { "questionId": 5, "selectedOptionIds": [30, 31, 32] }
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pointsAwarded").value(5))
                .andExpect(jsonPath("$.correctOptionIds").value(contains(30, 31, 33)))
                .andExpect(jsonPath("$.totalScore").value(15));
        mockMvc.perform(post("/api/attempts/{id}/finish", attemptId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finished").value(true))
                .andExpect(jsonPath("$.totalScore").value(15))
                .andExpect(jsonPath("$.maxScore").value(30))
                .andExpect(jsonPath("$.percent").value(50))
                .andExpect(jsonPath("$.answers.length()").value(2));
    }

    @Test
    @DisplayName("Die Spielansicht enthält keine Lösungen")
    void playViewHasNoSolutions() throws Exception {
        mockMvc.perform(post("/api/attempts")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "topicId": 1 }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.questions.length()").value(3))
                .andExpect(jsonPath("$..correct").doesNotExist())
                .andExpect(jsonPath("$..acceptedAnswers").doesNotExist())
                .andExpect(jsonPath("$..explanation").doesNotExist());
    }

    @Test
    @DisplayName("Regeln: doppelte Antwort, fremde Frage, Antwort nach Abschluss")
    void rules() throws Exception {
        Long attemptId = startAttempt(2L);
        String answerQuestion6 = """
                { "questionId": 6, "selectedOptionIds": [41] }
                """;
        answer(attemptId, answerQuestion6).andExpect(status().isOk());
        answer(attemptId, answerQuestion6)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("QUESTION_ALREADY_ANSWERED"));
        answer(attemptId, """
                { "questionId": 1, "selectedOptionIds": [1] }
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("QUESTION_NOT_IN_ATTEMPT"));
        mockMvc.perform(post("/api/attempts/{id}/finish", attemptId)).andExpect(status().isOk());
        answer(attemptId, """
                { "questionId": 4, "freeText": "select" }
                """)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ATTEMPT_ALREADY_FINISHED"));
    }

    @Test
    @DisplayName("Snapshot: Alte Versuche zeigen den alten Fragetext und die alten Punkte")
    void snapshotSurvivesQuestionChange() throws Exception {
        String originalText = jdbcTemplate.queryForObject("SELECT text FROM question WHERE id = 1",
                String.class);
        Long attemptId = startAttempt(1L);
        answer(attemptId, """
                { "questionId": 1, "selectedOptionIds": [1] }
                """).andExpect(status().isOk());
        try {
            // Ein Admin ändert die Frage nachträglich
            jdbcTemplate.update("UPDATE question SET text = ?, points = ? WHERE id = 1", "Geänderter Fragetext", 50);
            mockMvc.perform(get("/api/attempts/{id}", attemptId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.answers[0].questionText").value(originalText))
                    .andExpect(jsonPath("$.answers[0].pointsAwarded").value(10))
                    .andExpect(jsonPath("$.answers[0].maxPoints").value(10));
        } finally {
// aufräumen: @SpringBootTest rollt nichts zurück
            jdbcTemplate.update("UPDATE question SET text = ?, points = ? WHERE id = 1",
                    originalText, 10);
        }
    }
}