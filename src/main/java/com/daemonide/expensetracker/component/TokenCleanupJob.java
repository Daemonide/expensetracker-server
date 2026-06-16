package com.daemonide.expensetracker.component;

import com.daemonide.expensetracker.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class TokenCleanupJob {

    private final RefreshTokenRepository repository;

    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public void cleanup() {
        repository.deleteAllByExpiryDateBefore(
                Instant.now()
        );
    }
}