package com.example.order_service.config.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
@Slf4j
public class JwtWebFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtWebFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("Authorization header: {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("Token extracted: {}", token);

            try {
                Claims claims = jwtUtil.extractAllClaims(token);
                log.info("Claims extracted: subject={}, role={}",
                        claims.getSubject(), claims.get("role"));

                if (isTokenExpired(claims)) {
                    log.warn("Token expired");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                request.setAttribute("userId", claims.getSubject());
                request.setAttribute("role", claims.get("role", String.class));

            } catch (Exception e) {
                log.error("JWT validation failed", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            log.warn("Authorization header missing or doesn't start with Bearer");
        }

        filterChain.doFilter(request, response);
    }

    private boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        log.info("Token expiration: {}", expiration);
        return expiration != null && expiration.before(new Date());
    }
}
