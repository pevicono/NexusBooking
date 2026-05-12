package com.example.nexusbooking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Operation;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.method.HandlerMethod;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";
    private static final Pattern ROLE_PATTERN = Pattern.compile("hasRole\\('([^']+)'\\)");

    @Bean
    public OpenAPI nexusBookingOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .info(new Info()
                        .title("NexusBooking API")
                        .description("NexusBooking REST API with JWT auth and role-based access")
                        .version("v1.0.0"));
    }

    @Bean
    public OperationCustomizer operationSecurityCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            String preAuthorizeExpr = resolvePreAuthorize(handlerMethod);
            boolean isAuthPublicEndpoint = handlerMethod.getMethod().getDeclaringClass().getSimpleName().equals("AuthController");

            if (preAuthorizeExpr != null && !preAuthorizeExpr.isBlank()) {
                operation.addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
                Set<String> roles = extractRoles(preAuthorizeExpr);
                if (roles.isEmpty()) {
                    appendRoleDescription(operation, "Allowed users: authenticated");
                } else {
                    appendRoleDescription(operation, "Allowed roles: " + String.join(", ", roles));
                }
            } else if (!isAuthPublicEndpoint) {
                operation.addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
                appendRoleDescription(operation, "Allowed users: authenticated");
            } else {
                appendRoleDescription(operation, "Allowed users: public");
            }

            return operation;
        };
    }

    private String resolvePreAuthorize(HandlerMethod handlerMethod) {
        PreAuthorize methodAnnotation = handlerMethod.getMethodAnnotation(PreAuthorize.class);
        if (methodAnnotation != null) {
            return methodAnnotation.value();
        }

        PreAuthorize classAnnotation = handlerMethod.getBeanType().getAnnotation(PreAuthorize.class);
        if (classAnnotation != null) {
            return classAnnotation.value();
        }

        return null;
    }

    private Set<String> extractRoles(String expression) {
        Set<String> roles = new LinkedHashSet<>();
        Matcher matcher = ROLE_PATTERN.matcher(expression);
        while (matcher.find()) {
            roles.add(matcher.group(1));
        }
        return roles;
    }

    private void appendRoleDescription(Operation operation, String roleLine) {
        String current = operation.getDescription();
        if (current == null || current.isBlank()) {
            operation.setDescription(roleLine);
            return;
        }

        if (!current.contains("Allowed users:") && !current.contains("Allowed roles:")) {
            operation.setDescription(current + "\n\n" + roleLine);
        }
    }
}
