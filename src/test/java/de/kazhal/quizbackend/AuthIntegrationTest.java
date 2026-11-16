package de.kazhal.quizbackend;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static
        org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AuthIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Jeder Test bekommt eine eigene Adresse, weil @SpringBootTest nichts zurückrollt.
     */
    private static String uniqueEmail() {
        return "user-" + UUID.randomUUID() + "@example.com";
    }

    private void register(String email, String password) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "email": "%s", "password": "%s", "displayName": "Testperson" }
                                """.formatted(email, password)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("LEARNER"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    @DisplayName("Registrieren speichert einen BCrypt-Hash, nie das Passwort")
    void registerStoresHash() throws Exception {
        String email = uniqueEmail();
        register(email, "geheim123");
        String hash = jdbcTemplate.queryForObject(
                "SELECT password_hash FROM app_user WHERE email = ?", String.class, email);
        assertThat(hash).startsWith("$2").hasSize(60).doesNotContain("geheim123");
    }

    @Test
    @DisplayName("Dieselbe E-Mail zweimal: 409 EMAIL_TAKEN, auch mit anderer Schreibweise")
    void registerTwice() throws Exception {
        String email = uniqueEmail();
        register(email, "geheim123");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                                                { "email": "%s", "password": "anderes123", "displayName": "Doppelt" 
                                }
                                """.formatted(email.toUpperCase())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_TAKEN"));
    }

    @Test
    @DisplayName("Mitgeschickte Rolle wird ignoriert")
    void roleCannotBeChosen() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                                                { "email": "%s", "password": "geheim123", "displayName": "Mallory", 
                                "role": "ADMIN" }
                                """.formatted(uniqueEmail())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("LEARNER"));
    }

    @Test
    @DisplayName("/me: ohne Anmeldung 401, mit falschem Passwort 401, mit richtigem 200")
    void me() throws Exception {
        String email = uniqueEmail();
        register(email, "geheim123");
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/auth/me").with(httpBasic(email, "falsch123")))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/auth/me").with(httpBasic(email, "geheim123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("LEARNER"));
    }
}