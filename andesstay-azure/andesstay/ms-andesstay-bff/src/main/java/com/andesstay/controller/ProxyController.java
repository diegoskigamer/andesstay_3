package com.andesstay.controller;

import com.andesstay.config.ServiceUrlsConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;

/**
 * El BFF no reimplementa la lógica de negocio: valida el JWT
 * (SecurityConfig) y reenvía cada request al microservicio de dominio
 * correspondiente, preservando método, headers, body y query string.
 */
@RestController
public class ProxyController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ServiceUrlsConfig serviceUrls;

    public ProxyController(ServiceUrlsConfig serviceUrls) { this.serviceUrls = serviceUrls; }

    @RequestMapping("/api/reservations/**") public ResponseEntity<byte[]> reservations() { return forward(serviceUrls.getReservationsUrl()); }
    @RequestMapping("/api/catalog/**") public ResponseEntity<byte[]> catalog() { return forward(serviceUrls.getCatalogUrl()); }
    @RequestMapping("/api/audit/**") public ResponseEntity<byte[]> audit() { return forward(serviceUrls.getAuditUrl()); }
    @RequestMapping("/api/report/**") public ResponseEntity<byte[]> report() { return forward(serviceUrls.getReportUrl()); }

    private ResponseEntity<byte[]> forward(String targetBaseUrl) {
        HttpServletRequest original = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String path = original.getRequestURI();
        String query = original.getQueryString();
        String targetUrl = targetBaseUrl + path + (query != null ? "?" + query : "");

        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = original.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if ("host".equalsIgnoreCase(name) || "content-length".equalsIgnoreCase(name)) continue;
            headers.add(name, original.getHeader(name));
        }

        byte[] body;
        try { body = original.getInputStream().readAllBytes(); } catch (Exception e) { body = new byte[0]; }

        try {
            return restTemplate.exchange(targetUrl, HttpMethod.valueOf(original.getMethod()), new HttpEntity<>(body, headers), byte[].class);
        } catch (HttpStatusCodeException e) {
            // Devuelve tal cual el error del microservicio (404, 409, etc.) en vez de convertirlo en 500.
            return ResponseEntity.status(e.getStatusCode()).headers(e.getResponseHeaders() == null ? new HttpHeaders() : e.getResponseHeaders()).body(e.getResponseBodyAsByteArray());
        }
    }
}
