package com.andesstay.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class RabbitMqAdminService {
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${rabbitmq.management-url}") private String managementUrl;
    @Value("${rabbitmq.management-username}") private String username;
    @Value("${rabbitmq.management-password}") private String password;

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> listQueues() {
        return restTemplate.exchange(managementUrl + "/api/queues", HttpMethod.GET, new HttpEntity<>(authHeaders()), List.class).getBody();
    }
    @SuppressWarnings("unchecked")
    public Map<String, Object> overview() {
        return restTemplate.exchange(managementUrl + "/api/overview", HttpMethod.GET, new HttpEntity<>(authHeaders()), Map.class).getBody();
    }
    public void purgeQueue(String vhost, String queueName) {
        restTemplate.exchange(managementUrl + "/api/queues/" + vhost + "/" + queueName + "/contents", HttpMethod.DELETE, new HttpEntity<>(authHeaders()), Void.class);
    }
    private HttpHeaders authHeaders() { HttpHeaders h = new HttpHeaders(); h.setBasicAuth(username, password); return h; }
}
