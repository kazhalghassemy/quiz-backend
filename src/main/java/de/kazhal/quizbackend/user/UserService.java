package de.kazhal.quizbackend.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, Clock clock) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException(email);
        }
        AppUser user = new AppUser(
                email,
                passwordEncoder.encode(request.password()),
                request.displayName().trim(),
                Role.LEARNER,                       // die Rolle bestimmt nie der Client
                Instant.now(clock));
        return UserResponse.from(userRepository.save(user));
    }

    public UserResponse findUser(Long id) {
        return userRepository.findById(id)
                .map(UserResponse::from)
                .orElseThrow(() -> new IllegalStateException("Angemeldeter Benutzer " + id + "existiert nicht mehr"));
    }
}
