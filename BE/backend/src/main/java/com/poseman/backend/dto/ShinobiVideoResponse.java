package com.poseman.backend.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Shinobi VMS에서 반환되는 전체 JSON을 받아올 때 사용하는 DTO.
 * {
 * "ok": true,
 * "videos": [ { … }, { … } ],
 * "endIsStartTo": false
 * }
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShinobiVideoResponse {
    private boolean ok;

    // JSON의 “videos” 배열 전체를 매핑
    private List<ShinobiVideoInfo> videos;

    private boolean endIsStartTo;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ShinobiVideoInfo {
        // “ke”: store key (예: "group-key")
        private String ke;

        // “mid”: camera id (예: "monitor-id")
        private String mid;

        // “ext”: 파일 확장자 (예: "mp4")
        private String ext;

        // “time”: 클립 시작 시각 (ISO +09:00 포함)
        private String time;

        // “end”: 클립 종료 시각 (ISO +09:00 포함)
        private String end;

        // “filename”: 실제 영상 파일 이름 (예: "2025-05-31T00-02-31.mp4")
        private String filename;

        // “href”: 상대 경로 (예:
        // "/api-key/videos/group-key/monitor-id/2025-05-31T00-02-31.mp4")
        private String href;

        // 그 외 필요한 필드가 있으면 추가로 선언 가능
        // 예: private int size;
        // 예: private Map<String,Object> details;
        // 예: private Map<String, String> links;
    }
}
