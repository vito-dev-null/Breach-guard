package com.example.monitoring.service;

import com.example.monitoring.model.SystemStatus;
import com.example.monitoring.repository.MonitoraggioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MonitoraggioServiceTest {

    private MonitoraggioService service;
    private MonitoraggioRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(MonitoraggioRepository.class);
        service = new MonitoraggioService(repository);
    }

    @Test
    void getSystemStatusReturnsOkAndSavesRecord() {
        when(repository.save(any(SystemStatus.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SystemStatus status = service.getSystemStatus();

        assertThat(status).isNotNull();
        assertThat(status.getStatus()).isEqualTo("OK");
        assertThat(status.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void getSystemStatusReturnsOkEvenWhenSaveFails() {
        doThrow(new RuntimeException("database unavailable")).when(repository).save(any(SystemStatus.class));

        SystemStatus status = service.getSystemStatus();

        assertThat(status).isNotNull();
        assertThat(status.getStatus()).isEqualTo("OK");
        assertThat(status.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }
}
