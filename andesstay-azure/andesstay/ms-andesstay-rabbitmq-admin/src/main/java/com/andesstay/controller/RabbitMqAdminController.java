package com.andesstay.controller;
import com.andesstay.service.RabbitMqAdminService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/rabbitmq")
public class RabbitMqAdminController {
    private final RabbitMqAdminService adminService;
    public RabbitMqAdminController(RabbitMqAdminService s) { this.adminService = s; }
    @GetMapping("/overview") public Map<String, Object> overview() { return adminService.overview(); }
    @GetMapping("/queues") public List<Map<String, Object>> queues() { return adminService.listQueues(); }
    @DeleteMapping("/queues/{vhost}/{name}/purge") public void purgeQueue(@PathVariable String vhost, @PathVariable String name) { adminService.purgeQueue(vhost, name); }
}
