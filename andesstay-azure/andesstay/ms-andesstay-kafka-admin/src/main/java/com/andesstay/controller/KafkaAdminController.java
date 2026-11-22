package com.andesstay.controller;
import com.andesstay.service.KafkaAdminService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/kafka")
public class KafkaAdminController {
    private final KafkaAdminService adminService;
    public KafkaAdminController(KafkaAdminService s) { this.adminService = s; }
    @GetMapping("/overview") public Map<String, Object> overview() { return adminService.overview(); }
    @GetMapping("/topics") public List<Map<String, Object>> topics() { return adminService.listTopics(); }
    @GetMapping("/consumer-groups") public List<Map<String, Object>> consumerGroups() { return adminService.listConsumerGroups(); }
}
