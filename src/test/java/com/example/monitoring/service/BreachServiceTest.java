package com.example.monitoring.service;

import com.example.monitoring.dto.BreachResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BreachServiceTest {

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
    void checkEmailReturnsErrorForBlankEmail() {
        BreachResult result = service.checkEmail("   ");

        assertThat(result).isNotNull();
        assertThat(result.isBreached()).isFalse();
        assertThat(result.getBreaches()).isEmpty();
        assertThat(result.getNote()).contains("Inserisci un indirizzo email valido");
    }

    @Test
    void checkEmailReturnsCleanWhenApiReturnsNotFoundErrorJson() {
        String body = "{\"Error\": \"not found\"}";
        ResponseEntity<String> response = new ResponseEntity<>(body, HttpStatus.OK);
        when(restTemplate.exchange(any(), any(Class.class))).thenReturn(response);

        BreachResult result = service.checkEmail("test@example.com");

        assertThat(result).isNotNull();
        assertThat(result.isBreached()).isFalse();
        assertThat(result.getBreaches()).isEmpty();
        assertThat(result.getNote()).contains("Email pulita");
    }

    @Test
    void checkEmailReturnsBreachResultWhenApiReturnsBreachList() {
        String body = "{\"breaches\": [\"example.com\", \"example.org\"]}";
        ResponseEntity<String> response = new ResponseEntity<>(body, HttpStatus.OK);
        when(restTemplate.exchange(any(), any(Class.class))).thenReturn(response);

        BreachResult result = service.checkEmail("test@example.com");

        assertThat(result).isNotNull();
        assertThat(result.isBreached()).isTrue();
        assertThat(result.getBreaches()).hasSize(2);
        assertThat(result.getBreaches()).extracting("name").containsExactly("example.com", "example.org");
    }

    @Test
    void checkEmailReturnsTooManyRequestsMessageWhenApiThrowsTooManyRequests() {
        doThrow(HttpClientErrorException.create("Too Many Requests", HttpStatus.TOO_MANY_REQUESTS, "text/plain", null, null, null)).when(restTemplate)
                .exchange(any(), any(Class.class));

        BreachResult result = service.checkEmail("test@example.com");

        assertThat(result).isNotNull();
        assertThat(result.isBreached()).isFalse();
        assertThat(result.getNote()).contains("Troppe richieste");
    }
}
