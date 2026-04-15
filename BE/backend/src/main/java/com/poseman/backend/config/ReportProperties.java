package com.poseman.backend.config;

import java.util.UUID;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "report")
public class ReportProperties {
    private UUID systemUserId;

    public UUID getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(UUID systemUserId) {
        this.systemUserId = systemUserId;
    }
}
