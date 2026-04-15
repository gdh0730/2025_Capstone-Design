package com.poseman.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "shinobi")
public class ShinobiProperties {
    private String url;
    private String groupKey;
    private String apiKey; // 사용 시에만 활용
    private int windowseconds; // 기본값 60초

    // getters & setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public int getWindowSeconds() {
        return windowseconds;
    }

    public void setWindowSeconds(int windowseconds) {
        this.windowseconds = windowseconds;
    }
}
