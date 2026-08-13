package com.example.monitoring.service;

import com.example.monitoring.dto.BreachInfo;
import com.example.monitoring.dto.BreachResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BreachService {

    @Value("${xposedornot.api.url:https://api.xposedornot.com/v1/check-email}")
    private String xposedOrNotApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BreachResult checkEmail(String email) {
        String normalized = email == null ? "" : email.trim();
        if (normalized.isEmpty()) {
            return new BreachResult(email, false, new ArrayList<>(), "Inserisci un indirizzo email valido.");
        }

        try {
            String encodedEmail = URLEncoder.encode(normalized, StandardCharsets.UTF_8);
            String apiUrl = xposedOrNotApiUrl;
            if (!apiUrl.endsWith("/")) {
                apiUrl += "/";
            }
            apiUrl += encodedEmail;

            RequestEntity<Void> request = RequestEntity.get(URI.create(apiUrl))
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.USER_AGENT, "BreachGuardApp")
                    .build();

            ResponseEntity<String> response = restTemplate.exchange(request, String.class);
            System.out.println("[XposedOrNot DEBUG] Request URL: " + apiUrl);
            System.out.println("[XposedOrNot DEBUG] Response status: " + response.getStatusCodeValue() + " " + response.getStatusCode());
            System.out.println("[XposedOrNot DEBUG] Response body: " + response.getBody());
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> json = objectMapper.readValue(response.getBody(), new TypeReference<>() {
                });
                if (isNotFoundError(json)) {
                    return new BreachResult(email, false, new ArrayList<>(), "Email pulita: nessuna violazione nota secondo XposedOrNot.");
                }

                List<BreachInfo> breaches = extractBreaches(json);
                if (!breaches.isEmpty()) {
                    return new BreachResult(email, true, breaches, "Indirizzo compromesso: trovato da XposedOrNot.");
                }
                return new BreachResult(email, false, new ArrayList<>(), "Email pulita: nessuna violazione nota secondo XposedOrNot.");
            }

            if (response.getStatusCode() == HttpStatus.NO_CONTENT || response.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new BreachResult(email, false, new ArrayList<>(), "Email pulita: nessuna violazione nota secondo XposedOrNot.");
            }

            return new BreachResult(email, false, new ArrayList<>(), "Errore XposedOrNot: risposta HTTP " + response.getStatusCode());
        } catch (HttpClientErrorException.NotFound ignored) {
            return new BreachResult(email, false, new ArrayList<>(), "Email pulita: nessuna violazione nota secondo XposedOrNot.");
        } catch (HttpClientErrorException.TooManyRequests tooManyRequests) {
            return new BreachResult(email, false, new ArrayList<>(), "Troppe richieste verso XposedOrNot. Riprova tra qualche istante.");
        } catch (Exception ex) {
            return new BreachResult(email, false, new ArrayList<>(), "Errore durante il controllo XposedOrNot: " + ex.getMessage());
        }
    }

    private List<BreachInfo> extractBreaches(Map<String, Object> json) {
        List<BreachInfo> breaches = new ArrayList<>();
        if (json == null) {
            return breaches;
        }

        Object rawBreaches = json.get("breaches");
        if (rawBreaches instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof String breachName) {
                    breaches.add(new BreachInfo(breachName, "unknown", "unknown", "Breaches list returned by XposedOrNot."));
                } else if (item instanceof List<?> nestedList) {
                    for (Object nestedItem : nestedList) {
                        if (nestedItem instanceof String nestedName) {
                            breaches.add(new BreachInfo(nestedName, "unknown", "unknown", "Breaches list returned by XposedOrNot."));
                        }
                    }
                } else if (item instanceof Map<?, ?> breachMap) {
                    breaches.add(mapToBreachInfo(breachMap));
                }
            }
        } else if (rawBreaches instanceof Map<?, ?> breachMap) {
            breaches.add(mapToBreachInfo(breachMap));
        } else if (json.containsKey("breach")) {
            Object breachValue = json.get("breach");
            if (breachValue instanceof Map<?, ?> breachMap) {
                breaches.add(mapToBreachInfo(breachMap));
            } else if (breachValue instanceof String breachName) {
                breaches.add(new BreachInfo(breachName, "unknown", "unknown", "Breaches list returned by XposedOrNot."));
            }
        }

        return breaches;
    }

    private boolean isNotFoundError(Map<String, Object> json) {
        if (json == null) {
            return false;
        }

        Object error = json.get("Error");
        if (error instanceof String) {
            String errorValue = ((String) error).trim().toLowerCase();
            return errorValue.contains("not found") || errorValue.contains("notfound");
        }

        Object status = json.get("status");
        if (status instanceof String) {
            String statusValue = ((String) status).trim().toLowerCase();
            return statusValue.contains("not found") || statusValue.contains("error");
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private BreachInfo mapToBreachInfo(Map<?, ?> breachMap) {
        String name = extractString(breachMap.get("name"), breachMap.get("Name"), breachMap.get("title"), breachMap.get("Title"));
        String title = extractString(breachMap.get("title"), breachMap.get("Title"), breachMap.get("name"), breachMap.get("Name"));
        String domain = extractString(breachMap.get("domain"), breachMap.get("Domain"));
        String date = extractString(breachMap.get("date"), breachMap.get("Date"), breachMap.get("BreachDate"), breachMap.get("breachDate"));
        String description = extractString(breachMap.get("description"), breachMap.get("Description"), breachMap.get("summary"), breachMap.get("Summary"));
        String source = extractString(breachMap.get("source"), breachMap.get("Source"), breachMap.get("breachName"));

        Map<String, Object> additionalInfo = new java.util.LinkedHashMap<>();
        if (breachMap != null) {
            for (Map.Entry<?, ?> entry : breachMap.entrySet()) {
                if (!(entry.getKey() instanceof String key)) {
                    continue;
                }
                String normalizedKey = key.trim();
                if (normalizedKey.isEmpty()) {
                    continue;
                }
                if (normalizedKey.equalsIgnoreCase("name")
                        || normalizedKey.equalsIgnoreCase("title")
                        || normalizedKey.equalsIgnoreCase("domain")
                        || normalizedKey.equalsIgnoreCase("date")
                        || normalizedKey.equalsIgnoreCase("breachdate")
                        || normalizedKey.equalsIgnoreCase("description")
                        || normalizedKey.equalsIgnoreCase("summary")
                        || normalizedKey.equalsIgnoreCase("source")
                        || normalizedKey.equalsIgnoreCase("breachname")) {
                    continue;
                }
                Object value = entry.getValue();
                if (value != null) {
                    additionalInfo.put(normalizedKey, value);
                }
            }
        }

        return new BreachInfo(name, title, domain, date, description, source, additionalInfo);
    }

    private String extractString(Object... values) {
        for (Object value : values) {
            if (value instanceof String str && !str.isBlank()) {
                return str.trim();
            }
        }
        return "unknown";
    }
}
