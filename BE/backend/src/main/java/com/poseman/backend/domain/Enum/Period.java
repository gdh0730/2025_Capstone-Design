package com.poseman.backend.domain.Enum;

import java.time.LocalDateTime;

public enum Period {
    DAILY, WEEKLY;

    public LocalDateTime getStart(LocalDateTime now) {
        return (this == DAILY) ? now.minusDays(1) : now.minusWeeks(1);
    }
}
// 이 Enum은 리포트 생성 시 기간을 나타내기 위해 사용됩니다.
// DAILY는 일별 리포트, WEEKLY는 주간 리포트를 의미합니다.