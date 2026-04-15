package com.poseman.backend.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poseman.backend.config.AppProperties;
import com.poseman.backend.config.ShinobiProperties;
import com.poseman.backend.domain.entity.EventLog;
import com.poseman.backend.domain.repository.EventLogRepository;
import com.poseman.backend.dto.ClipResponse;
import com.poseman.backend.dto.ShinobiVideoResponse;
import com.poseman.backend.dto.ShinobiVideoResponse.ShinobiVideoInfo;
import com.poseman.backend.service.VideoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class VideoStreamController {

        private final VideoService videoService;
        private final ShinobiProperties shinobiProps;
        private final AppProperties appProps;
        private final EventLogRepository eventLogRepository;

        /**
         * 프론트에서 호출하는 “영상 스트리밍” 엔드포인트.
         * 
         * @param monitorId - Cam ID (예: "cam3")
         * @param filename  - 실제 파일 이름 (예: "2025-05-31T00-02-31.mp4")
         * @param from      - ISO timestamp
         * @param to        - ISO timestamp
         * @return - Spring Resource 형태로 Shinobi로부터 받은 비디오 바이트를 프록시
         */
        /**
         * (1) 단일 영상 스트리밍을 프록시하는 메서드
         */
        @GetMapping("/api/video/stream/{monitorId}/{filename:.+}")
        public ResponseEntity<Resource> streamVideo(
                        @PathVariable String monitorId,
                        @PathVariable String filename,
                        @RequestParam String from,
                        @RequestParam String to) throws IOException {

                // VideoService.getVideoStreamFromShinobi(...) 호출
                ResponseEntity<InputStreamResource> shinobiResponse = videoService.getVideoStreamFromShinobi(monitorId,
                                filename, from, to);

                // 응답된 Content-Type을 그대로 내려줌
                MediaType mediaType = shinobiResponse.getHeaders().getContentType();
                if (mediaType == null) {
                        mediaType = MediaType.APPLICATION_OCTET_STREAM;
                }

                return ResponseEntity
                                .ok()
                                .contentType(mediaType)
                                .body(shinobiResponse.getBody());
        }

        /**
         * 이벤트 발생 시, 해당 이벤트의 모니터에서 발생한 클립 목록을 조회하는 엔드포인트.
         * 
         * @param eventId - 이벤트 ID (UUID)
         * @return - ClipResponse DTO에 담긴 클립 목록
         * @throws IllegalArgumentException - 이벤트가 존재하지 않을 경우
         * @throws RestClientException      - Shinobi API 호출 실패 시
         * @throws IOException              - URL 인코딩 실패 시
         * @return - ClipResponse DTO에 담긴 클립 목록
         */
        /**
         * (2) 이벤트 ID를 받아 “클립 목록(여러 개)”을 JSON으로 반환
         */
        @GetMapping("/api/video/list/event/{eventId}")
        public ResponseEntity<ClipResponse> listClipsByEvent(
                        @PathVariable("eventId") String eventId) {

                // 1) DB에서 이벤트 조회
                EventLog event = eventLogRepository.findById(UUID.fromString(eventId))
                                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

                // 2) 모니터 ID과 이벤트 발생 시각
                String monitorId = event.getCamera().getCameraId();
                LocalDateTime evtTs = event.getTimestamp();

                // 3) 이벤트 ± windowseconds 구간 계산
                LocalDateTime fromTime = evtTs.minusSeconds(shinobiProps.getWindowSeconds());
                LocalDateTime toTime = evtTs.plusSeconds(shinobiProps.getWindowSeconds());
                String fromStr = fromTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                String toStr = toTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                // 4) VideoService를 통해 “Shinobi 클립 목록” 조회
                ShinobiVideoResponse rawResponse;
                try {
                        rawResponse = videoService.getClipListFromShinobi(monitorId, fromStr, toStr);
                } catch (RuntimeException ex) {
                        // Shinobi 호출 실패 시 502로 응답
                        return ResponseEntity.status(502).build();
                }

                // 5) ShinobiVideoInfo → ClipResponse.Clip 변환
                List<ClipResponse.Clip> clipList = new ArrayList<>();
                String backendBase = appProps.getBackendUrl(); // ex: "http://localhost:8081"

                for (ShinobiVideoInfo info : rawResponse.getVideos()) {
                        ClipResponse.Clip clip = new ClipResponse.Clip();
                        clip.setType(info.getExt());
                        clip.setStart(info.getTime());
                        clip.setEnd(info.getEnd());
                        clip.setFilename(info.getFilename());

                        // 6) “백엔드 프록시 스트리밍 URL” 생성
                        // GET
                        // {backendBase}/api/video/stream/{monitorId}/{filename}?start={fromStr}&end={toStr}
                        String encMid = URLEncoder.encode(monitorId, StandardCharsets.UTF_8);
                        String encFile = URLEncoder.encode(info.getFilename(), StandardCharsets.UTF_8);
                        String encFrom = URLEncoder.encode(fromStr, StandardCharsets.UTF_8);
                        String encTo = URLEncoder.encode(toStr, StandardCharsets.UTF_8);

                        String proxyUrl = String.format(
                                        "%s/api/video/stream/%s/%s?start=%s&end=%s",
                                        backendBase, encMid, encFile, encFrom, encTo);

                        clip.setUrl(proxyUrl);
                        clipList.add(clip);
                }

                // 7) DTO에 담아서 응답
                ClipResponse response = new ClipResponse();
                response.setClips(clipList);
                return ResponseEntity.ok(response);
        }
}
