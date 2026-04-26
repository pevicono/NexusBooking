package com.example.nexusbooking.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Filter que detecta i bloqueja peticions simulades o manipulades
 * - Valida headers de segureca
 * - Detecta patrons sospitosos en requests
 * - Protegeix contra ataques comuns
 */
public class RequestValidationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestValidationFilter.class);
    
    private static final Set<String> SUSPICIOUS_USER_AGENTS = new HashSet<>();
    private static final Set<String> SUSPICIOUS_PATTERNS = new HashSet<>();
    
    static {
        // User agents de bots/scrapers
        SUSPICIOUS_USER_AGENTS.add("sqlmap");
        SUSPICIOUS_USER_AGENTS.add("nikto");
        SUSPICIOUS_USER_AGENTS.add("nmap");
        SUSPICIOUS_USER_AGENTS.add("masscan");
        SUSPICIOUS_USER_AGENTS.add("curl");
        SUSPICIOUS_USER_AGENTS.add("wget");
        
        // Patrons sospitosos en URLs
        SUSPICIOUS_PATTERNS.add("'");
        SUSPICIOUS_PATTERNS.add("\"");
        SUSPICIOUS_PATTERNS.add(";");
        SUSPICIOUS_PATTERNS.add("<");
        SUSPICIOUS_PATTERNS.add(">");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Validar User-Agent
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && !userAgent.isEmpty()) {
            String lowerAgent = userAgent.toLowerCase();
            for (String suspicious : SUSPICIOUS_USER_AGENTS) {
                if (lowerAgent.contains(suspicious)) {
                    log.warn("Suspicious User-Agent detected: {} from {}", userAgent, request.getRemoteAddr());
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                    return;
                }
            }
        }
        
        // Validar que POST/PUT/DELETE requereixen Content-Type valida
        String method = request.getMethod();
        if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
            String contentType = request.getContentType();
            if (contentType == null || (!contentType.contains("application/json") && !contentType.contains("application/x-www-form-urlencoded"))) {
                log.warn("Invalid Content-Type for {} request from {}", method, request.getRemoteAddr());
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Content-Type");
                return;
            }
        }
        
        // Validar Query String per SQL injection
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            for (String pattern : SUSPICIOUS_PATTERNS) {
                if (queryString.contains(pattern)) {
                    // Verificar que no sea una URL encoding valida
                    if (!queryString.contains("%") || !queryString.contains("%27")) { // %27 es '
                        log.warn("Suspicious query pattern detected: {} from {}", queryString, request.getRemoteAddr());
                        // No bloquejar, pero logar
                    }
                }
            }
        }
        
        // Agregar headers de seguretat a la resposta
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Strict-Transport-Security", "max-age=63072000; includeSubDomains");
        
        filterChain.doFilter(request, response);
    }
}
