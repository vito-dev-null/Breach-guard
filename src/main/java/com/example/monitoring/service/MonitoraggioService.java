package com.example.monitoring.service;

import com.example.monitoring.model.SystemStatus;
import com.example.monitoring.repository.MonitoraggioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MonitoraggioService {

    private final MonitoraggioRepository repository;

    public MonitoraggioService(MonitoraggioRepository repository) {
        this.repository = repository;
    }

    public SystemStatus getSystemStatus() {
        SystemStatus status = SystemStatus.builder()
                .status("OK")
                .timestamp(LocalDateTime.now())
                .build();

        // Save a record (optional)
        try {
            repository.save(status);
        } catch (Exception ignored) {
        }

        return status;
    }
}
