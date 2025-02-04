package com.example.Rider.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator implements RequestMatcher {
    public static final List<String> openApiEndpoints = List.of(
            "/api/v1/login",
            "/v3/api-docs",
            "/swagger-ui"
    );

    public Predicate<HttpServletRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(request.getRequestURI()::startsWith);

    @Override
    public boolean matches(HttpServletRequest request) {
        return !isSecured.test(request);
    }
}

