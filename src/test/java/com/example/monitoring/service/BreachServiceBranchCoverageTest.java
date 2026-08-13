package com.example.monitoring.service;

import com.example.monitoring.dto.BreachInfo;
import com.example.monitoring.dto.BreachResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BreachServiceBranchCoverageTest {

    private BreachService service;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        service = new BreachService();
        restTemplate = mock(RestTemplate.class);
        ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(service, "objectMapper", new ObjectMapper());
        ReflectionTestUtils.setField(service, "xposedOrNotApiUrl", "https://api.xposedornot.com/v1/check-email");
    }

    @Test
    void checkEmailReturnsCleanWhenStatusFieldReportsNotFound() {
        when(restTemplate.exchange(any(), any(Class.class)))
                .thenReturn(new ResponseEntity<>("{\"status\":\"error not found\"}", HttpStatus.OK));

        BreachResult result = service.checkEmail("test@example.com");

        assertThat(result.isBreached()).isFalse();
        assertThat(result.getBreaches()).isEmpty();
        assertThat(result.getNote()).contains("Email pulita");
    }

    @Test
    void checkEmailHandlesStructuredBreachMapResponse() {
        String body = "{\"breaches\":[{\"name\":\"Example\",\"title\":\"Example title\",\"domain\":\"example.com\",\"date\":\"2024-01-01\",\"description\":\"Leaked\",\"source\":\"XposedOrNot\",\"extra\":\"value\"}]}";
        when(restTemplate.exchange(any(), any(Class.class)))
                .thenReturn(new ResponseEntity<>(body, HttpStatus.OK));

        BreachResult result = service.checkEmail("test@example.com");

        assertThat(result.isBreached()).isTrue();
        assertThat(result.getBreaches()).hasSize(1);
        BreachInfo breach = result.getBreaches().getFirst();
        assertThat(breach.getName()).isEqualTo("Example");
        assertThat(breach.getTitle()).isEqualTo("Example title");
        assertThat(breach.getDomain()).isEqualTo("example.com");
        assertThat(breach.getAdditionalInfo()).containsEntry("extra", "value");
    }

    @Test
    void checkEmailReturnsCleanWhenApiReturnsNotFoundStatus() {
        when(restTemplate.exchange(any(), any(Class.class)))
                .thenReturn(new ResponseEntity<>("", HttpStatus.NOT_FOUND));

        BreachResult result = service.checkEmail("test@example.com");

        assertThat(result.isBreached()).isFalse();
        assertThat(result.getNote()).contains("Email pulita");
    }

    @Test
    void checkEmailReturnsErrorWhenResponseStatusIsUnexpected() {
        when(restTemplate.exchange(any(), any(Class.class)))
                .thenReturn(new ResponseEntity<>("{\"message\":\"unexpected\"}", HttpStatus.BAD_GATEWAY));

        BreachResult result = service.checkEmail("test@example.com");

        assertThat(result.isBreached()).isFalse();
        assertThat(result.getNote()).contains("Errore XposedOrNot: risposta HTTP 502 BAD_GATEWAY");
    }
}
