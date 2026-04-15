package com.poseman.backend.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poseman.backend.domain.Enum.Period;
import com.poseman.backend.domain.entity.EventLog;
import com.poseman.backend.domain.entity.Report;
import com.poseman.backend.domain.repository.EventLogRepository;
import com.poseman.backend.domain.repository.ReportRepository;
import com.poseman.backend.domain.repository.UserRepository;
import com.poseman.backend.dto.ReportResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

        private final EventLogRepository eventLogRepository;
        private final ReportRepository reportRepository;
        private final UserRepository userRepository;
        private final ObjectMapper objectMapper; // Jackson JSON 매퍼

        /**
         * Controller에서 GET /api/report?storeId=xxx&period=daily 요청 시 호출
         *
         * @param request - { storeId, period }
         * @param userId  - JWT 인증 후 얻은 관리자 계정 UUID
         * @return ReportResponseDTO
         */
        @Transactional
        public ReportResponseDTO generateReport(String storeId, Period period, UUID userId)
                        throws JsonProcessingException {
                // ─────────── 0) 기존 리포트 조회 ───────────
                // 저장된 리포트 중 storeId & period(“daily” or “weekly”)가 같은 것이 있는지 확인
                List<Report> existing = reportRepository.findByStoreIdAndTypeOrderByCreatedAtDesc(storeId,
                                period.toString());
                if (!existing.isEmpty()) {
                        // 이미 생성된 리포트 중 가장 최신(가장 마지막 인덱스) 하나를 반환
                        // (만약 생성일(createdAt) 순으로 정렬하고 싶다면 findByStoreIdAndType 호출 시 Sort 옵션을 주거나,
                        // 여기서 existing.stream().max(Comparator.comparing(Report::getCreatedAt)) 등을 이용해도
                        // 됨)
                        Report rep = existing.get(0);

                        // 기존 리포트 엔티티를 DTO로 변환
                        ReportResponseDTO response = new ReportResponseDTO();
                        response.setReportId(rep.getReportId());
                        response.setStoreId(rep.getStoreId());
                        response.setType(rep.getType().toString());
                        response.setCreatedAt(rep.getCreatedAt());

                        // dataJson(String) → Map<String,Object> 로 역역직렬화
                        Map<String, Object> map = objectMapper.readValue(rep.getDataJson(),
                                        new TypeReference<Map<String, Object>>() {
                                        });
                        response.setSummaryData(map);
                        return response;
                }

                // ─────────── 1) 기존 리포트 없으면 새로 생성 ───────────
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime from = period.getStart(now);
                // 1-1) 해당 기간 이벤트 통계 조회
                List<EventLog> events = eventLogRepository.findByCamera_StoreIdAndTimestampBetweenOrderByTimestampDesc(
                                storeId,
                                from, now);
                Map<String, Long> behaviorCount = events.stream()
                                .collect(Collectors.groupingBy(EventLog::getBehavior, Collectors.counting()));

                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("totalEvents", events.size());
                dataMap.put("behaviorStats", behaviorCount);
                dataMap.put("generatedAt", now.toString());

                // 1-2) 엔티티 저장
                Report report = new Report();
                report.setStoreId(storeId);
                report.setType(period);
                report.setCreatedAt(now);
                report.setDataJson(objectMapper.writeValueAsString(dataMap));
                report.setUser(userRepository.findById(userId).orElse(null));

                reportRepository.save(report);

                // 1-3) DTO 생성 및 반환
                ReportResponseDTO response = new ReportResponseDTO();
                response.setReportId(report.getReportId());
                response.setStoreId(report.getStoreId());
                response.setType(report.getType().toString());
                response.setCreatedAt(report.getCreatedAt());
                response.setSummaryData(dataMap);

                return response;
        }
}
