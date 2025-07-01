package com.randevu.randevusistemibackend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for API diagnostics and troubleshooting
 */
@RestController
@RequestMapping("/api/diagnostics")
@RequiredArgsConstructor
@Tag(name = "API Diagnostics", description = "Endpoints for API diagnostics and troubleshooting")
@Slf4j
public class ApiDiagnosticsController {

    @Value("${server.port:8080}")
    private String serverPort;

    @Operation(summary = "Get server information", description = "Returns information about the server configuration")
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getServerInfo(HttpServletRequest request) {
        Map<String, Object> info = new HashMap<>();
        
        // Basic server info
        info.put("serverPort", serverPort);
        info.put("requestScheme", request.getScheme());
        info.put("requestServerName", request.getServerName());
        info.put("requestServerPort", request.getServerPort());
        info.put("requestContextPath", request.getContextPath());
        info.put("requestServletPath", request.getServletPath());
        info.put("requestPathInfo", request.getPathInfo());
        info.put("requestQueryString", request.getQueryString());
        info.put("requestUrl", request.getRequestURL().toString());
        info.put("requestUri", request.getRequestURI());
        info.put("forwardedProto", request.getHeader("X-Forwarded-Proto"));
        info.put("forwardedHost", request.getHeader("X-Forwarded-Host"));
        info.put("forwardedPort", request.getHeader("X-Forwarded-Port"));
        
        log.info("Server diagnostics requested: {}", info);
        
        return ResponseEntity.ok(info);
    }
}
