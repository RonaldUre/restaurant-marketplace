package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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
        if (!props.isEnabled()) {
            log.debug("Refresh token cleanup job disabled.");
            return;
        }

        final LocalDateTime cutoff = LocalDateTime.now(ZoneOffset.UTC).minusDays(props.getRetentionDays());
        final int batchSize = props.getBatchSize();

        int totalDeleted = 0;

        // (1) Expirados antes del cutoff
        totalDeleted += deleteBatched("""
                    DELETE FROM refresh_tokens
                     WHERE id IN (
                       SELECT id
                         FROM refresh_tokens
                        WHERE expires_at < ?
                        ORDER BY id
                        LIMIT ?
                     )
                """, cutoff, batchSize);

        // (2) Revocados antiguos (creados antes del cutoff)
        totalDeleted += deleteBatched("""
                    DELETE FROM refresh_tokens
                     WHERE id IN (
                       SELECT id
                         FROM refresh_tokens
                        WHERE revoked = TRUE
                          AND created_at < ?
                        ORDER BY id
                        LIMIT ?
                     )
                """, cutoff, batchSize);

        if (totalDeleted > 0) {
            log.info("RefreshTokenCleanupJob purged {} rows (cutoff={}, batchSize={})",
                    totalDeleted, cutoff, batchSize);
        } else {
            log.debug("RefreshTokenCleanupJob nothing to purge (cutoff={}, batchSize={})", cutoff, batchSize);
        }
    }

    /**
     * Ejecuta borrados por lotes hasta que el último lote tenga menos filas que el
     * límite.
     */
    private int deleteBatched(String sql, LocalDateTime cutoff, int batchSize) {
        int deletedTotal = 0;
        while (true) {
            int deleted = jdbc.update(sql, ps -> {
                // 1) cutoff (timestamp)
                ps.setObject(1, cutoff);
                // 2) limit
                ps.setInt(2, batchSize);
            });
            deletedTotal += deleted;
            if (deleted < batchSize)
                break; // último lote
        }
        return deletedTotal;
    }

}
