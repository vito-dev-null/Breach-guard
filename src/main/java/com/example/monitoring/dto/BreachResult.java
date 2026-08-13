package com.example.monitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BreachResult {
    private String email;
    private boolean breached;
    private List<BreachInfo> breaches;
    private String note;
}
