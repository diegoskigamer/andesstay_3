package com.andesstay.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
@RestController
public class HealthController {
    @GetMapping("/api/notify/status") public Map<String, String> status() { return Map.of("service", "ms-andesstay-notify", "status", "listening"); }
}
