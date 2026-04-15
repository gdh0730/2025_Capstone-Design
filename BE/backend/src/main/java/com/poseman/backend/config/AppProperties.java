package com.poseman.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {
    /** 프론트가 호출할 백엔드의 베이스 URL */
    private String backend_url;
    /** 백엔드가 호출할 프론트의 베이스 URL */
    private String frontend_url;

    public String getBackendUrl() {
        return backend_url;
    }

    public void setBackendUrl(String backend_url) {
        this.backend_url = backend_url;
    }

    public String getFrontendUrl() {
        return frontend_url;
    }

    public void setFrontendUrl(String frontend_url) {
        this.frontend_url = frontend_url;
    }
}
