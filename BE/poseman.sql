-- 사용자 계정 (관리자)
CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) CHECK (role IN ('ADMIN', 'OPERATOR')) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 카메라 정보
CREATE TABLE cameras (
    camera_id VARCHAR(50) PRIMARY KEY,
    store_id VARCHAR(50) NOT NULL,
    location TEXT,
    status BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 카메라 설정 정보
CREATE TABLE settings (
    setting_id UUID PRIMARY KEY,
    camera_id VARCHAR(50) UNIQUE,
    sensitivity FLOAT CHECK (sensitivity >= 0 AND sensitivity <= 1),
    alert_enabled BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (camera_id) REFERENCES cameras(camera_id) ON DELETE CASCADE
);

-- 이상행동 이벤트 로그
CREATE TABLE event_logs (
    event_id UUID PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    camera_id VARCHAR(50),
    behavior VARCHAR(100),
    confidence FLOAT CHECK (confidence >= 0 AND confidence <= 1),
    severity INTEGER CHECK (severity BETWEEN 1 AND 5),
    FOREIGN KEY (camera_id) REFERENCES cameras(camera_id) ON DELETE SET NULL
);

-- 실시간 알림 로그
CREATE TABLE alerts (
    alert_id UUID PRIMARY KEY,
    event_id UUID UNIQUE,
    alert_type VARCHAR(50),
    sent_time TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES event_logs(event_id) ON DELETE CASCADE
);

-- 이벤트 기반 저장 영상 정보
CREATE TABLE video_clips (
    clip_id UUID PRIMARY KEY,
    event_id UUID UNIQUE,
    file_path TEXT NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES event_logs(event_id) ON DELETE CASCADE
);

-- 이벤트 기반 스피커 경고 기록
CREATE TABLE speaker_triggers (
    trigger_id UUID PRIMARY KEY,
    event_id UUID UNIQUE,
    trigger_time TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES event_logs(event_id) ON DELETE CASCADE
);

-- 관리자 접근 로그
CREATE TABLE access_logs (
    log_id UUID PRIMARY KEY,
    user_id UUID,
    accessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    action TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- 사용자 분석 및 리포트
CREATE TABLE reports (
    report_id UUID PRIMARY KEY,
    user_id UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    type VARCHAR(20),  -- 예: daily, weekly
    data_json TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);
