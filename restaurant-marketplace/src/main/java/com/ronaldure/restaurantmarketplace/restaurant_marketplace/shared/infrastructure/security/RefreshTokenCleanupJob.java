package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "jobs.refresh-token-cleanup.enabled", havingValue = "true", matchIfMissing = true)
public class RefreshTokenCleanupJob {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenCleanupJob.class);

    private final JdbcTemplate jdbc;
    private final RefreshTokenCleanupProperties props;

    public RefreshTokenCleanupJob(JdbcTemplate jdbc, RefreshTokenCleanupProperties props) {
        this.jdbc = jdbc;
        this.props = props;
    }

    /** Corre diario; el cron se toma de application.properties. */
    @Scheduled(cron = "${jobs.refresh-token-cleanup.cron:0 30 3 * * *}")
    public void purgeOldTokens() {
        final int retentionDays = props.getRetentionDays();
        final int batchSize = props.getBatchSize();

        if (!props.isEnabled()) {
            log.debug("Refresh token cleanup job disabled.");
            return;
        }

        int totalDeleted = 0;

        // 1) Expirados más antiguos que la ventana
        while (true) {
            int deleted = jdbc.update("""
                DELETE FROM refresh_tokens
                 WHERE expires_at < (NOW() - INTERVAL ? DAY)
                 LIMIT ?
                """, retentionDays, batchSize);
            totalDeleted += deleted;
            if (deleted < batchSize) break;
        }

        // 2) Revocados creados antes de la ventana
        while (true) {
            int deleted = jdbc.update("""
                DELETE FROM refresh_tokens
                 WHERE revoked = 1
                   AND created_at < (NOW() - INTERVAL ? DAY)
                 LIMIT ?
                """, retentionDays, batchSize);
            totalDeleted += deleted;
            if (deleted < batchSize) break;
        }

        if (totalDeleted > 0) {
            log.info("RefreshTokenCleanupJob purged {} rows (retentionDays={}, batchSize={})",
                    totalDeleted, retentionDays, batchSize);
        } else {
            log.debug("RefreshTokenCleanupJob nothing to purge (retentionDays={}, batchSize={})",
                    retentionDays, batchSize);
        }
    }
}
