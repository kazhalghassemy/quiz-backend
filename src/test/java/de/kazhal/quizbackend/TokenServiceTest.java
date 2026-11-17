package de.kazhal.quizbackend;

import de.kazhal.quizbackend.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    private static final Instant NOW = Instant.parse("2026-11-10T10:00:00Z");
    @Mock
    private AuthTokenRepository authTokenRepository;
    @Mock
    private UserRepository userRepository;
    private TokenService tokenService;
    private AppUser anna;
    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        tokenService = new TokenService(authTokenRepository, userRepository,
                new AuthProperties(Duration.ofHours(8)), clock);
        anna = new AppUser("anna@example.com", "$2a$10$hash", "Anna", Role.LEARNER, NOW);
        ReflectionTestUtils.setField(anna, "id", 7L);
    }
    @Test
    @DisplayName("Speichert nur den Hash des Tokens, mit Ablauf in 8 Stunden")
    void issueTokenStoresOnlyHash() {
        when(userRepository.getReferenceById(7L)).thenReturn(anna);
        IssuedToken issued = tokenService.issueToken(7L);
        ArgumentCaptor<AuthToken> saved = ArgumentCaptor.forClass(AuthToken.class);
        verify(authTokenRepository).save(saved.capture());
        assertThat(issued.token()).hasSize(43);
        assertThat(saved.getValue().getTokenHash())
                .hasSize(64)
                .isEqualTo(TokenService.hash(issued.token()))
                .isNotEqualTo(issued.token());
        assertThat(issued.expiresAt()).isEqualTo(NOW.plus(Duration.ofHours(8)));
    }
    @Test
    @DisplayName("Zwei Tokens sind nie gleich")
    void tokensAreRandom() {
        assertThat(TokenService.generateToken()).isNotEqualTo(TokenService.generateToken());
    }
    @Test
    @DisplayName("Gültiges Token liefert den Benutzer, ohne Passwort-Hash")
    void validToken() {
        AuthToken token = new AuthToken(TokenService.hash("abc"), anna, NOW, NOW.plusSeconds(60));

        when(authTokenRepository.findWithUserByTokenHash(TokenService.hash("abc"))).thenReturn(Optional.of(token));
        Optional<QuizUserDetails> user = tokenService.authenticate("abc");
        assertThat(user).isPresent();
        assertThat(user.get().id()).isEqualTo(7L);
        assertThat(user.get().getPassword()).isNull();
    }
    @Test
    @DisplayName("Abgelaufenes Token wird abgelehnt")
    void expiredToken() {
        AuthToken token = new AuthToken(TokenService.hash("abc"), anna, NOW.minusSeconds(120),
                NOW.minusSeconds(60));
        when(authTokenRepository.findWithUserByTokenHash(any())).thenReturn(Optional.of(token));
        assertThat(tokenService.authenticate("abc")).isEmpty();
    }
    @Test
    @DisplayName("Widerrufenes Token wird abgelehnt")
    void revokedToken() {
        AuthToken token = new AuthToken(TokenService.hash("abc"), anna, NOW, NOW.plusSeconds(60));
        token.revoke();
        when(authTokenRepository.findWithUserByTokenHash(any())).thenReturn(Optional.of(token));
        assertThat(tokenService.authenticate("abc")).isEmpty();
    }
}