package com.poseman.backend.service;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.poseman.backend.config.ReportProperties;
import com.poseman.backend.domain.Enum.Period;
import com.poseman.backend.domain.repository.CameraRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportScheduler {

    private final ReportService reportService;
    private final CameraRepository cameraRepository;
    private final ReportProperties reportProperties; // 주입

    // 매일 새벽 3시에 자동으로 일간 리포트를 생성
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    public void generateDailyReports() {
        List<String> storeIds = cameraRepository.findDistinctStoreIds();
        for (String storeId : storeIds) {
            try {
                reportService.generateReport(storeId, Period.DAILY, reportProperties.getSystemUserId());
            } catch (Exception ex) {
                // 예외가 나면 로그만 남기고 다음 매장으로 넘어간다
                LoggerFactory.getLogger(ReportScheduler.class)
                        .error("리포트 생성 실패 [storeId=" + storeId + "]: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
