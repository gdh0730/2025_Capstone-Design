package com.poseman.backend.dto;

import java.util.List;

import lombok.Data;

/**
 * VideoController에서 최종적으로 반환할 DTO.
 * 프론트에서는 이 JSON을 받아서, video player 등에 전달하면 된다.
 */
@Data
public class ClipResponse {
    private List<Clip> clips;

    @Data
    public static class Clip {
        private String type; // 파일 확장자, 예: "mp4"
        private String url; // 실제 전송용 URL (절대경로)
        private String start; // 클립 시작 시각 (ISO 포맷)
        private String end; // 클립 종료 시각 (ISO 포맷)
        private String filename; // 원본 Shinobi 파일 이름 (옵션)
    }
}
