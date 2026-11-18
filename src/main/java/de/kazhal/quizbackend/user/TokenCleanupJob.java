package de.kazhal.quizbackend.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.time.Instant;
@Component
public class TokenCleanupJob {
    private static final Logger log = LoggerFactory.getLogger(TokenCleanupJob.class);
    private final AuthTokenRepository authTokenRepository;
    private final Clock clock;
    public TokenCleanupJob(AuthTokenRepository authTokenRepository, Clock clock) {
        this.authTokenRepository = authTokenRepository;
        this.clock = clock;
    }
    @Scheduled(cron = "${quiz.auth.cleanup-cron:0 0 3 * * *}")
    @Transactional
    public void deleteExpiredAndRevokedTokens() {
        int deleted = authTokenRepository.deleteExpiredOrRevoked(Instant.now(clock));
        log.info("{} abgelaufene oder widerrufene Tokens gelöscht", deleted);
    }
}