package com.poseman.backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poseman.backend.dto.EventCreateRequest;
import com.poseman.backend.dto.EventResponse;
import com.poseman.backend.service.EventLogUiService;
import com.poseman.backend.service.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventLogUiService eventLogUiService;

    // 1) AI 추론 결과 수신 (POST /api/events)
    @PostMapping
    public ResponseEntity<Void> receiveEvent(@RequestBody @Valid EventCreateRequest eventDto) {
        eventService.saveEvent(eventDto);
        return ResponseEntity.ok().build();
    }

    // 2) 이벤트 이력 조회 (GET /api/events?date=...&cameraId=...)
    @GetMapping
    public ResponseEntity<List<EventResponse>> getEventsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String cameraId) {

        List<EventResponse> list = eventLogUiService.getEventsByDate(date, cameraId);
        return ResponseEntity.ok(list);
    }

    // 3) 이벤트 이력 조회 (GET /api/events/all)
    @GetMapping("/all")
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        List<EventResponse> all = eventLogUiService.getAllEvents();
        return ResponseEntity.ok(all);
    }
}
