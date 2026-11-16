package de.kazhal.quizbackend.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.Locale;

@Component
public class AdminAccountInitializer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminAccountInitializer.class);
    private final AdminAccountProperties properties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public AdminAccountInitializer(AdminAccountProperties properties, UserRepository userRepository,
                                   PasswordEncoder passwordEncoder, Clock clock) {
        this.properties = properties;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (properties.email() == null || properties.password() == null ||
                properties.password().isBlank()) {
            log.info("Kein Admin-Konto konfiguriert (quiz.admin.email / quiz.admin.password)");
            return;
        }
        String email = properties.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            return;
        }
        userRepository.save(new AppUser(email, passwordEncoder.encode(properties.password()),
                "Administrator", Role.ADMIN, Instant.now(clock)));
        log.info("Admin-Konto {} angelegt", email);
    }
}