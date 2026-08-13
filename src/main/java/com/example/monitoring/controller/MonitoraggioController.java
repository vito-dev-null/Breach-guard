package com.example.monitoring.controller;

import com.example.monitoring.model.SystemStatus;
import com.example.monitoring.service.MonitoraggioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MonitoraggioController {

    private final MonitoraggioService service;

    public MonitoraggioController(MonitoraggioService service) {
        this.service = service;
    }

    @GetMapping("/status")
    public ResponseEntity<SystemStatus> status() {
        SystemStatus s = service.getSystemStatus();
        return ResponseEntity.ok(s);
    }
}
