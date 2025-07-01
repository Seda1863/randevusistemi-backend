package com.randevu.randevusistemibackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Enumeration;

/**
 * Filter that logs all HTTP requests and responses.
 * For large request/response bodies, only a preview will be logged.
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
    
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final int MAX_PAYLOAD_LENGTH = 1000;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Wrap request and response to access the body multiple times
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Execute request
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            // Log after the request is processed
            logRequest(requestWrapper, startTime);
            logResponse(responseWrapper, System.currentTimeMillis() - startTime);
            
            // Copy content to the original response
            responseWrapper.copyBodyToResponse();
        }
    }
    
    private void logRequest(ContentCachingRequestWrapper request, long startTime) {
        StringBuilder logMessage = new StringBuilder("\n--- HTTP REQUEST LOG ---\n");
        logMessage.append("URI: ").append(request.getRequestURI()).append("\n");
        logMessage.append("Method: ").append(request.getMethod()).append("\n");
        logMessage.append("Time: ").append(startTime).append("\n");
        logMessage.append("Client IP: ").append(request.getRemoteAddr()).append("\n");
        logMessage.append("User Agent: ").append(request.getHeader("User-Agent")).append("\n");
        
        // Log request headers
        logMessage.append("Request Headers: {\n");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            // Skip logging sensitive headers like Authorization
            if (shouldLogHeader(headerName)) {
                logMessage.append("  ").append(headerName).append(": ")
                         .append(request.getHeader(headerName)).append("\n");
            } else {
                logMessage.append("  ").append(headerName).append(": [PROTECTED]\n");
            }
        }
        logMessage.append("}\n");
        
        // Log request body
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            logMessage.append("Request Body: ");
            String bodyContent = getContentAsString(content, request.getCharacterEncoding());
            if (bodyContent.contains("password") || bodyContent.contains("secret")) {
                logMessage.append("[PROTECTED CONTENT]\n");
            } else {
                logMessage.append(bodyContent.length() > MAX_PAYLOAD_LENGTH 
                    ? bodyContent.substring(0, MAX_PAYLOAD_LENGTH) + "...[truncated]" 
                    : bodyContent).append("\n");
            }
        }
        
        log.info(logMessage.toString());
    }
    
    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        StringBuilder logMessage = new StringBuilder("\n--- HTTP RESPONSE LOG ---\n");
        logMessage.append("Status: ").append(response.getStatus()).append("\n");
        logMessage.append("Duration: ").append(duration).append("ms\n");
        
        // Log response headers
        logMessage.append("Response Headers: {\n");
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            logMessage.append("  ").append(headerName).append(": ")
                     .append(response.getHeader(headerName)).append("\n");
        }
        logMessage.append("}\n");
        
        // Log response body
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            logMessage.append("Response Body: ");
            String bodyContent = getContentAsString(content, response.getCharacterEncoding());
            logMessage.append(bodyContent.length() > MAX_PAYLOAD_LENGTH 
                ? bodyContent.substring(0, MAX_PAYLOAD_LENGTH) + "...[truncated]" 
                : bodyContent).append("\n");
        }
        
        log.info(logMessage.toString());
    }
    
    private boolean shouldLogHeader(String headerName) {
        return !headerName.equalsIgnoreCase("Authorization") && 
               !headerName.equalsIgnoreCase("Cookie") &&
               !headerName.equalsIgnoreCase("Set-Cookie");
    }
    
    private String getContentAsString(byte[] content, String contentEncoding) {
        if (content == null || content.length == 0) {
            return "";
        }
        try {
            return new String(content, contentEncoding != null ? contentEncoding : "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "Unsupported Encoding";
        }
    }
}