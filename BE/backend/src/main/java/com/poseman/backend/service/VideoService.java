package com.poseman.backend.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.poseman.backend.config.ShinobiProperties;
import com.poseman.backend.dto.ShinobiVideoResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VideoService {

    /**
     * 1) 정적 경로 기반 영상 반환:
     * URL 패턴: /videos/{storeId}/{camId}/{date}/{time}.mp4
     * 예: storeId=store3, camId=cam3, date="2025-05-21", time="14-30"
     * 실제 파일 경로: /var/shinobi/videos/store3/cam3/2025-05-21/14-30.mp4
     */
    /*
     * private final Path BASE_VIDEO_DIR = Paths.get("/var/shinobi/videos");
     * 
     * public ResponseEntity<Resource> serveStaticVideo(
     * String storeId,
     * String camId,
     * String date,
     * String time
     * ) throws IOException {
     * // 1) 파일 시스템 경로 구성
     * Path videoPath = BASE_VIDEO_DIR
     * .resolve(storeId)
     * .resolve(camId)
     * .resolve(date)
     * .resolve(time + ".mp4");
     * 
     * // 2) 존재 여부 검사
     * if (!Files.exists(videoPath) || !Files.isReadable(videoPath)) {
     * return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
     * }
     * 
     * // 3) Resource 생성
     * Resource video = new FileSystemResource(videoPath);
     * // 4) 적절한 MIME 타입 설정
     * MediaType mediaType = MediaTypeFactory.getMediaType(video)
     * .orElse(MediaType.APPLICATION_OCTET_STREAM);
     * 
     * return ResponseEntity.ok()
     * .contentType(mediaType)
     * .body(video);
     * }
     */

    /**
     * 2) VMS(API) 방식 영상 요청 프록시:
     */
    private final RestTemplate restTemplate;
    private final ShinobiProperties shinobiProps;

    /**
     * Shinobi VMS API를 통해서 JSON을 받아오고,
     * 받은 ShinobiVideoResponse를 ClipResponse로 변환하여 반환한다.
     *
     * @param monitorId (예: "cam3")
     * @param from      (ISO timestamp, 예: "2025-05-31T00:00:00")
     * @param to        (ISO timestamp)
     * @return ShinobiVideoResponse (ok, videos 리스트 포함)
     * @return ClipResponse DTO
     */
    public ShinobiVideoResponse getClipListFromShinobi(String monitorId, String from, String to) {
        // 1) URL 구성 (기존 잘못된 순서 대신 올바른 순서로!)
        //
        // http://<SHINOBI_HOST>/
        // <API_KEY>/
        // videos/
        // <GROUP_KEY>/
        // <MONITOR_ID>
        // ?start=<from>&end=<to>
        //
        // 1) Shinobi 기본 URL/키 가져오기
        String baseUrl = shinobiProps.getUrl(); // 예: "http://localhost:8080"
        String apiKey = shinobiProps.getApiKey(); // 예: "api-key"
        String groupKey = shinobiProps.getGroupKey(); // 예: "poseman"

        // 2) URL 인코딩
        // String encMonitor = URLEncoder.encode(monitorId, StandardCharsets.UTF_8);
        // String encFrom = URLEncoder.encode(from, StandardCharsets.UTF_8);
        // String encTo = URLEncoder.encode(to, StandardCharsets.UTF_8);

        // 3) 완전한 Shinobi 목록 조회 URL 조합
        // 예:
        // http://localhost:8080/<apiKey>/videos/<groupKey>/<encMonitor>?start=<encFrom>&end=<encTo>
        String listUrl = String.format(
                "%s/%s/videos/%s/%s",
                baseUrl,
                apiKey,
                groupKey,
                monitorId);

        // 2) UriComponentsBuilder로 쿼리파라미터 부착 → 파라미터 값만 인코딩
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(listUrl)
                .queryParam("start", from)
                .queryParam("end", to);
        String finalUrl = builder.toUriString();

        // 4) RestTemplate으로 JSON(ShinobiVideoResponse) 호출
        ShinobiVideoResponse rawResponse;
        try {
            rawResponse = restTemplate.getForObject(finalUrl, ShinobiVideoResponse.class);
        } catch (RestClientException e) {
            throw new RuntimeException("VMS API 호출 실패: " + e.getMessage(), e);
        }

        // 5) null 체크 및 ok 여부 검증
        if (rawResponse == null || !rawResponse.isOk()) {
            throw new RuntimeException("VMS 응답 오류 또는 ok=false: " + rawResponse);
        }

        return rawResponse;
    }

    /**
     * Shinobi VMS API를 호출해서 특정 모니터의 단일 영상(바이트 스트림)을 받아온다.
     *
     * @param monitorId (예: "cam3")
     * @param filename  (예: "2025-05-31T00-02-31.mp4")
     * @param from      (ISO timestamp)
     * @param to        (ISO timestamp)
     * @return org.springframework.core.io.InputStreamResource (바이트 스트림)
     */
    public ResponseEntity<InputStreamResource> getVideoStreamFromShinobi(
            String monitorId, String filename, String from, String to) {
        String baseUrl = shinobiProps.getUrl();
        String apiKey = shinobiProps.getApiKey();
        String groupKey = shinobiProps.getGroupKey();

        // 인코딩
        String encMonitor = URLEncoder.encode(monitorId, StandardCharsets.UTF_8);
        String encFile = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        String encFrom = URLEncoder.encode(from, StandardCharsets.UTF_8);
        String encTo = URLEncoder.encode(to, StandardCharsets.UTF_8);

        // Shinobi 단일 스트리밍 URL 조합
        // 예:
        // http://localhost:8080/<apiKey>/videos/<groupKey>/<encMonitor>/<encFile>?start=<encFrom>&end=<encTo>
        String shinobiUrl = String.format(
                "%s/%s/videos/%s/%s/%s?start=%s&end=%s",
                baseUrl,
                apiKey,
                groupKey,
                encMonitor,
                encFile,
                encFrom,
                encTo);

        try {
            // RestTemplate으로 InputStreamResource 형태로 가져옴
            return restTemplate.getForEntity(
                    shinobiUrl,
                    InputStreamResource.class);
        } catch (RestClientException ex) {
            // Shinobi 호출 실패 시 예외 던지기
            throw new RuntimeException("VMS 영상 스트리밍 호출 실패: " + ex.getMessage(), ex);
        }
    }
}