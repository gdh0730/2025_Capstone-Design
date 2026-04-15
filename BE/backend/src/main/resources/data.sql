-- src/main/resources/data.sql

-- 1) 혹시라도 이미 레코드가 남아 있다면 모두 삭제(진짜 빈 테이블을 만든 뒤에 INSERT)
DELETE FROM cameras;

-- 2) 개발용 샘플 카메라 삽입
INSERT INTO cameras (camera_id, store_id, location, status, created_at)
VALUES ('cam1', 'store1', '매장 입구', TRUE, CURRENT_TIMESTAMP);

INSERT INTO cameras (camera_id, store_id, location, status, created_at)
VALUES ('yUjrVNe2p2', 'store42', '계산대', TRUE, CURRENT_TIMESTAMP);
