package com.example.monitoring.controller;

import com.example.monitoring.dto.BreachResult;
import com.example.monitoring.service.BreachService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BreachApiController {

    private final BreachService breachService;

    public BreachApiController(BreachService breachService) {
        this.breachService = breachService;
    }

    @GetMapping("/check")
    public BreachResult checkEmail(@RequestParam("email") String email) {
        return breachService.checkEmail(email);
    }
}
