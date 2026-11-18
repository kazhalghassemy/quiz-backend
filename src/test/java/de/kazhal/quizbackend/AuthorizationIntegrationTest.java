package de.kazhal.quizbackend;

import com.jayway.jsonpath.JsonPath;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
// @AutoConfigureMockMvc: Import mit Alt+Enter
import java.util.UUID;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest(properties = {
        "quiz.admin.email=admin@test.local",
        "quiz.admin.password=admin12345"
})
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AuthorizationIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private String login(String email, String password) throws Exception {
        String json = mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "email": "%s", "password": "%s" }
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.parse(json).read("$.token");
    }
    private String newLearner() throws Exception {
        String email = "learner-" + UUID.randomUUID() + "@example.com";
        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "email": "%s", "password": "geheim123", "displayName": "Lernende" }
                                """.formatted(email)))
                .andExpect(status().isCreated());
        return login(email, "geheim123");
    }
    private Long startAttempt(String auth) throws Exception {
        String json = mockMvc.perform(post("/api/attempts")
                        .header("Authorization", auth)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "topicId": 2 }
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.parse(json).read("$.attemptId", Long.class);
    }
    @Test
    @DisplayName("Admin-Sicht: ohne Anmeldung 401, als LEARNER 403, als ADMIN 200")
    void adminView() throws Exception {
        mockMvc.perform(get("/api/topics/1/questions"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/topics/1/questions").header("Authorization", newLearner()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
        mockMvc.perform(get("/api/topics/1/questions").header("Authorization",
                        login("admin@test.local", "admin12345")))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Nur ADMIN legt Themen an")
    void createTopic() throws Exception {
        String body = """
                { "name": "Thema %s" }
                """.formatted(UUID.randomUUID());
        mockMvc.perform(post("/api/topics").header("Authorization", newLearner())
                        .contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/topics").header("Authorization", login("admin@test.local",
                                "admin12345"))
                        .contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
    }
    @Test
    @DisplayName("Fremde Versuche sind für Lernende unsichtbar (404), Admins dürfen sie lesen")
    void foreignAttempt() throws Exception {
        String anna = newLearner();
        String ben = newLearner();
        Long annasAttempt = startAttempt(anna);
        mockMvc.perform(get("/api/attempts/{id}", annasAttempt).header("Authorization", ben))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/api/attempts/{id}/answers", annasAttempt).header("Authorization", ben)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "questionId": 4, "freeText": "select" }
                                """))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/attempts/{id}", annasAttempt).header("Authorization", anna))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/attempts/{id}", annasAttempt)
                        .header("Authorization", login("admin@test.local", "admin12345")))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Nach dem Logout ist das Token sofort ungültig")
    void logout() throws Exception {
        String auth = newLearner();
        mockMvc.perform(get("/api/auth/me").header("Authorization", auth)).andExpect(status().isOk());
        mockMvc.perform(post("/api/auth/logout").header("Authorization",
                auth)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/auth/me").header("Authorization",
                auth)).andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("CORS: Preflight von localhost:4200 erlaubt, von fremder Origin abgelehnt")
    void cors() throws Exception {
        mockMvc.perform(options("/api/topics")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
        mockMvc.perform(options("/api/topics")
                        .header("Origin", "https://evil.example")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }
}