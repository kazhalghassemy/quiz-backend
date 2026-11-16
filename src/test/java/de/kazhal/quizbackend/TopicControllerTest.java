package de.kazhal.quizbackend;

import de.kazhal.quizbackend.topic.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
// Spring Boot 4:  import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
// Spring Boot 3:  import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TopicController.class)
class TopicControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TopicService topicService;

    @Test
    @DisplayName("POST mit gültigem Body: 201, Location-Header und JSON")
    void createTopic() throws Exception {
        when(topicService.createTopic(any())).thenReturn(new TopicResponse(5L, "Spring Boot", "Beans und JPA"));
        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "Spring Boot", "description": "Beans und JPA" }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/topics/5"))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Spring Boot"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST mit gültigem Body: 201, Location-Header und JSON")
    void createTopicBlankName() throws Exception {
        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "   ", "description": "Nur Leerzeichen" }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("Name darf nicht leer sein"));
        verifyNoInteractions(topicService);
    }

    @Test
    @DisplayName("GET unbekanntes Thema: 404 mit Code und instance")
    void getTopicNotFound() throws Exception {
        when(topicService.findTopic(999L)).thenThrow(new TopicNotFoundException(999L));
        mockMvc.perform(get("/api/topics/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TOPIC_NOT_FOUND"))
                .andExpect(jsonPath("$.detail").value("Thema mit ID 999 nicht gefunden"))
                .andExpect(jsonPath("$.instance").value("/api/topics/999"));
    }

    @Test
    @DisplayName("POST mit vergebenem Namen: 409")
    void createTopicNameTaken() throws Exception {
        when(topicService.createTopic(any())).thenThrow(new TopicNameAlreadyExistsException("Java Grundlagen"));
        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "Java-Grundlagen" }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("TOPIC_NAME_TAKEN"));
    }


    @Test
    @DisplayName("POST ohne Anmeldung: 401")
    void createTopicWithoutLogin() throws Exception {
        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "Spring Boot" }
                                """))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(topicService);
    }

}
