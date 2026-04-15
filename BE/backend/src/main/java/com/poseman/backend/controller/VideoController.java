package com.poseman.backend.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poseman.backend.config.ShinobiProperties;
import com.poseman.backend.domain.entity.EventLog;
import com.poseman.backend.domain.repository.EventLogRepository;
import com.poseman.backend.dto.ClipResponse;
import com.poseman.backend.dto.ShinobiVideoResponse;
import com.poseman.backend.service.VideoService;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
public class VideoController {

    private final VideoService videoService;
    private final ShinobiProperties shinobiProps;
    private final EventLogRepository eventLogRepository; // 추가 주입

    /*
     * // 1) 정적 경로 기반 영상 요청 (/videos/{store}/{cam}/{timestamp}.mp4)
     * 
     * @GetMapping("/videos/{store}/{cam}/{timestamp}.mp4")
     * public ResponseEntity<Resource> getStaticVideo(
     * 
     * @PathVariable String store,
     * 
     * @PathVariable String cam,
     * 
     * @PathVariable String timestamp) {
     * return videoService.serveStaticVideo(store, cam, timestamp);
     * }
     */

    // 2) VMS API를 프록시 방식으로 호출 (/vms/api/clip?camera=...&from=...&to=...)
    // (1) 기존: from/to 직접 지정 방식 유지
    @GetMapping("/vms/api/clip")
    public ResponseEntity<ClipResponse> getClipFromVms(
            @RequestParam @NotBlank String camera,
            @RequestParam @NotBlank String from,
            @RequestParam @NotBlank String to) {

        // VideoService로 Shinobi 클립 목록 조회 후 응답으로 내려줌
        ClipResponse clipResponse = new ClipResponse();
        clipResponse.setClips(
                videoService
                        .getClipListFromShinobi(camera, from, to)
                        .getVideos() // List<ShinobiVideoInfo>
                        .stream()
                        .map(info -> {
                            ClipResponse.Clip clip = new ClipResponse.Clip();
                            clip.setType(info.getExt());
                            clip.setUrl(buildAbsoluteShinobiUrl(info.getHref())); // 기존 방식 그대로
                            clip.setStart(info.getTime());
                            clip.setEnd(info.getEnd());
                            clip.setFilename(info.getFilename());
                            return clip;
                        })
                        .toList());
        return ResponseEntity.ok(clipResponse);
    }

    // (2) 새로 추가: 이벤트 ID 하나만 주면, 백엔드가 from/to 계산 후 Shinobi 호출
    @GetMapping("/vms/api/clip/event/{eventId}")
    public ResponseEntity<ClipResponse> getClipByEvent(
            @PathVariable("eventId") @NotBlank String eventId) {

        EventLog event = eventLogRepository.findById(UUID.fromString(eventId))
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

        String cameraId = event.getCamera().getCameraId();
        LocalDateTime eventTime = event.getTimestamp();

        LocalDateTime fromTime = eventTime.minusSeconds(shinobiProps.getWindowSeconds());
        LocalDateTime toTime = eventTime.plusSeconds(shinobiProps.getWindowSeconds());

        String fromStr = fromTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String toStr = toTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // VideoService로 Shinobi 호출
        ShinobiVideoResponse rawResponse = videoService.getClipListFromShinobi(cameraId, fromStr, toStr);

        ClipResponse response = new ClipResponse();
        response.setClips(
                rawResponse.getVideos().stream().map(info -> {
                    ClipResponse.Clip clip = new ClipResponse.Clip();
                    clip.setType(info.getExt());
                    clip.setUrl(buildAbsoluteShinobiUrl(info.getHref()));
                    clip.setStart(info.getTime());
                    clip.setEnd(info.getEnd());
                    clip.setFilename(info.getFilename());
                    return clip;
                }).toList());
        return ResponseEntity.ok(response);
    }

    // (예시) shinobiProps.getUrl() + info.getHref() 를 절대 URL로 바꿔주는 헬퍼
    private String buildAbsoluteShinobiUrl(String href) {
        String baseUrl = shinobiProps.getUrl();
        if (href.startsWith("/")) {
            return baseUrl + href;
        } else {
            return baseUrl + "/" + href;
        }
    }
}