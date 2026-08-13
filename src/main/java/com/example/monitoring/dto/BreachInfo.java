package com.example.monitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BreachInfo {
    private String name;
    private String title;
    private String domain;
    private String breachDate;
    private String description;
    private String source;
    private Map<String, Object> additionalInfo;

    public BreachInfo(String name, String domain, String breachDate, String description) {
        this.name = name;
        this.title = null;
        this.domain = domain;
        this.breachDate = breachDate;
        this.description = description;
        this.source = "XposedOrNot";
        this.additionalInfo = new LinkedHashMap<>();
    }
}
