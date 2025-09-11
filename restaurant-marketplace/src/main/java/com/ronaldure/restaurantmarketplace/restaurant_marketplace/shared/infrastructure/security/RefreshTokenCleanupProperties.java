package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Config for the refresh-token cleanup job.
 */
@ConfigurationProperties(prefix = "jobs.refresh-token-cleanup")
public class RefreshTokenCleanupProperties {

    /** Enable/disable this specific job (además del toggle global de scheduling). */
    private boolean enabled = true;

    /** Ventana de retención en días (60 por defecto). */
    private int retentionDays = 60;

    /** Tamaño de lote para DELETE (5000 por defecto). */
    private int batchSize = 5000;

    /** Cron del job. OJO: solo para metadata; el @Scheduled usa property placeholder. */
    private String cron = "0 30 3 * * *";

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getRetentionDays() { return retentionDays; }
    public void setRetentionDays(int retentionDays) { this.retentionDays = retentionDays; }
    public int getBatchSize() { return batchSize; }
    public void setBatchSize(int batchSize) { this.batchSize = batchSize; }
    public String getCron() { return cron; }
    public void setCron(String cron) { this.cron = cron; }
}
