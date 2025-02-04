package com.example.Rider.security;

import com.example.Rider.config.RouteValidator;
import com.example.Rider.dto.response.common.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LogManager.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Autowired
    private RouteValidator routeValidator;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (routeValidator.isSecured.test(request)) {
            //Authorization
            String requestHeader = request.getHeader("Authorization");

            String username = null;
            String token = null;

            if (requestHeader != null && requestHeader.startsWith("Bearer")) {
                //looking good
                token = requestHeader.substring(7);
                try {
                    username = this.jwtHelper.getUsernameFromToken(token);
                } catch (IllegalArgumentException e) {
                    LOGGER.info("Illegal Argument while fetching the username !!");
                    resolver.resolveException(request, createResponse(response, "Invalid Token!"), null, e);
                    return;
                } catch (ExpiredJwtException e) {
                    LOGGER.info("Given jwt token is expired !!");
                    resolver.resolveException(request, createResponse(response, "Token expired. Please login!"), null, e);
                    return;
                } catch (MalformedJwtException e) {
                    LOGGER.info("Some changed has done in token !! Invalid Token");
                    resolver.resolveException(request, createResponse(response, "Invalid Token!"), null, e);
                    return;
                } catch (Exception e) {
                    LOGGER.info("Invalid Token");
                    resolver.resolveException(request, createResponse(response, "Invalid Token!"), null, e);
                    return;
                }
            } else {
                LOGGER.info("Invalid Header Value!!");
                resolver.resolveException(request, createResponse(response, "Token not provided or Invalid token!"), null, new RuntimeException("Invalid Header Value!!"));
                return;
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                //fetch user detail from username
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);

                if (validateToken) {
                    //set the authentication
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    LOGGER.info("Validation fails !!");
                    resolver.resolveException(request, createResponse(response, "Invalid Token!"), null, new RuntimeException());
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private HttpServletResponse createResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        response.addHeader("Content-Type", "application/json");
        response.getWriter().write(objectMapper.writeValueAsString(
                new Response<>(
                        String.valueOf(HttpStatus.UNAUTHORIZED.value()), false, message, message
                ))
        );

        return response;
    }
}
