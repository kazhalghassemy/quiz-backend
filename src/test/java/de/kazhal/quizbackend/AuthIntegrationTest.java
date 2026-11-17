package de.kazhal.quizbackend;


import de.kazhal.quizbackend.user.TokenService;
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

    private String login(String email, String password) throws Exception {
        String json = mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "email": "%s", "password": "%s" }
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn().getResponse().getContentAsString();
        return com.jayway.jsonpath.JsonPath.parse(json).read("$.token");
    }
    @Test
    @DisplayName("Login liefert ein Token, mit dem /me funktioniert")
    void loginAndMe() throws Exception {
        String email = uniqueEmail();
        register(email, "geheim123");
        String token = login(email, "geheim123");
        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }
    @Test
    @DisplayName("In der Datenbank steht nur der SHA-256-Hash des Tokens")
    void tokenStoredAsHash() throws Exception {
        String email = uniqueEmail();
        register(email, "geheim123");
        String token = login(email, "geheim123");
        Integer withHash = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM auth_token WHERE token_hash = ?", Integer.class,
                TokenService.hash(token));
        Integer withPlainToken = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM auth_token WHERE token_hash = ?", Integer.class, token);
        assertThat(withHash).isEqualTo(1);
        assertThat(withPlainToken).isZero();
    }
    @Test
    @DisplayName("Falsches Passwort: 401 INVALID_CREDENTIALS")
    void wrongPassword() throws Exception {
        String email = uniqueEmail();
        register(email, "geheim123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "email": "%s", "password": "falsch123" }
                                """.formatted(email)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }
    @Test
    @DisplayName("Ohne, mit kaputtem und mit abgelaufenem Token: 401 UNAUTHORIZED")
    void invalidTokens() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer gibt-es-nicht"))
                .andExpect(status().isUnauthorized());
        String email = uniqueEmail();
        register(email, "geheim123");
        String token = login(email, "geheim123");
        jdbcTemplate.update("UPDATE auth_token SET expires_at = now() - interval '1 minute' WHERE token_hash = ?",
                TokenService.hash(token));
        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("Öffentliche Endpunkte funktionieren auch mit kaputtem Token")
    void publicEndpointWithBrokenToken() throws Exception {
        mockMvc.perform(get("/api/topics").header("Authorization", "Bearer kaputt"))
                .andExpect(status().isOk());
    }
}