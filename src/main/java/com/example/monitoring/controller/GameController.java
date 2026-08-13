package com.example.monitoring.controller;

import com.example.monitoring.dto.BreachResult;
import com.example.monitoring.service.BreachService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GameController {

    private final BreachService breachService;

    public GameController(BreachService breachService) {
        this.breachService = breachService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/github")
    public String github(@RequestParam(value = "email", required = false) String email, Model model) {
        if (email != null && !email.isBlank()) {
            BreachResult result = breachService.checkEmail(email);
            model.addAttribute("result", result);
        }
        return "github";
    }

    @PostMapping("/check")
    public String checkEmail(@RequestParam("email") String email, Model model) {
        BreachResult result = breachService.checkEmail(email);
        model.addAttribute("result", result);
        return "result";
    }
}
