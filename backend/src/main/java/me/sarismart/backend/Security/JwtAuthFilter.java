package me.sarismart.backend.Security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = null;

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
            String[] fallbackHeaders = {"X-Authorization", "Auth", "X-Auth-Token"};
            for (String header : fallbackHeaders) {
                String fallbackHeader = request.getHeader(header);
                if (fallbackHeader != null && !fallbackHeader.isEmpty()) {
                    if (fallbackHeader.startsWith("Bearer ")) {
                        token = fallbackHeader.substring(7);
                    } else {
                        token = fallbackHeader;
                    }
                    System.out.println("Token found in fallback header: " + header);
                    break;
                }
            }
        }

        if (token != null) {
            try {
                Claims claims = jwtUtil.validateToken(token);

                String userId = claims.getSubject();
                String role = claims.get("role", String.class);

                if (role == null) {
                    throw new RuntimeException("Missing role claim in token");
                }

                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, Collections.singletonList(authority));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                request.setAttribute("user_id", userId);

            } catch (RuntimeException e) {
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token.");
                return;
            }
        } else {
            System.out.println("No valid token found in headers");
        }

        filterChain.doFilter(request, response);
    }
}