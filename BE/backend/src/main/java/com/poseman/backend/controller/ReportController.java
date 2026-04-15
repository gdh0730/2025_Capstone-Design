package com.poseman.backend.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poseman.backend.domain.Enum.Period;
import com.poseman.backend.dto.ReportResponseDTO;
import com.poseman.backend.security.UserPrincipal;
import com.poseman.backend.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<ReportResponseDTO> getReport(
            @RequestParam String storeId,
            @RequestParam Period period,
            @AuthenticationPrincipal UserPrincipal user // ★ com.poseman.backend.security.UserPrincipal
    ) throws Exception { // generateReport에서 JsonProcessingException 던질 수 있음
        UUID userId = user.getUserId(); // UserPrincipal에서 꺼낸 실제 UUID
        ReportResponseDTO report = reportService.generateReport(storeId, period, userId);
        return ResponseEntity.ok(report);
    }
}
