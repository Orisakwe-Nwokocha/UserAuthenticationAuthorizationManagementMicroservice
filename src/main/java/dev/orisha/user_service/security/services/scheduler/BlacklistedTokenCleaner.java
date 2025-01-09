package dev.orisha.user_service.security.services.scheduler;

import dev.orisha.user_service.security.data.repositories.BlacklistedTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static java.time.Instant.now;

@Service
@Slf4j
public class BlacklistedTokenCleaner {

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Autowired
    public BlacklistedTokenCleaner(final BlacklistedTokenRepository blacklistedTokenRepository) {
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    @Scheduled(cron = "${task.blackListedToken.cleaner.cron}")
    public void deleteExpiredTokens() {
        log.info("Tracking and deleting expired user tokens");
        var blacklist = blacklistedTokenRepository.findAll();
        blacklist.stream()
                .filter(blacklistedToken -> now().isAfter(blacklistedToken.getExpiresAt()))
                .forEach(blacklistedTokenRepository::delete);
        log.info("Expired user tokens successfully tracked and deleted");
    }

}
