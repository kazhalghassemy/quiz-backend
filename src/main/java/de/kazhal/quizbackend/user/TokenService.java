package de.kazhal.quizbackend.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

/**
 * issueToken: Zufallstoken erzeugen, nur den Hash speichern, den Klartext einmal zurückgeben.
 * authenticate: Token hashen, in der Datenbank suchen, Gültigkeit prüfen. Das Ergebnis ist ein Benutzer oder eine leere Schachtel.
 * generateToken: 32 Bytes echter Zufall, als URL-sicherer Text.
 * hash: SHA-256, als 64 Zeichen Hex-Text. Dabei mit Bindestrich: "SHA-256".
 * Die Clock gibt es, damit du Zeiten im Test steuern kannst.
 */
@Service
public class TokenService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final AuthTokenRepository authTokenRepository;
    private final UserRepository userRepository;
    private final AuthProperties authProperties;
    private final Clock clock;
    public TokenService(AuthTokenRepository authTokenRepository, UserRepository userRepository,
                        AuthProperties authProperties, Clock clock) {
        this.authTokenRepository = authTokenRepository;
        this.userRepository = userRepository;
        this.authProperties = authProperties;
        this.clock = clock;
    }
    @Transactional
    public IssuedToken issueToken(Long userId) {
        AppUser user = userRepository.getReferenceById(userId);
        String token = generateToken();
        Instant now = Instant.now(clock);
        Instant expiresAt = now.plus(authProperties.tokenValidity());
        authTokenRepository.save(new AuthToken(hash(token), user, now, expiresAt));
        return new IssuedToken(token, expiresAt);
    }
    @Transactional(readOnly = true)
    public Optional<QuizUserDetails> authenticate(String token) {
        Instant now = Instant.now(clock);
        return authTokenRepository.findWithUserByTokenHash(hash(token))
                .filter(t -> t.isValidAt(now))
                .map(t -> QuizUserDetails.withoutPassword(t.getUser()));
    }
    public static String generateToken() {
        byte[] bytes = new byte[32];                     // 256 Bit
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    public static String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 ist nicht verfügbar", e);
        }
    }
}
