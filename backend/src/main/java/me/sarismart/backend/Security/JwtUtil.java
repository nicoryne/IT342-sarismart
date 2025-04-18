package me.sarismart.backend.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import me.sarismart.backend.Config.AppConfig;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final AppConfig appConfig;

    @Autowired
    public JwtUtil(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public Claims validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(appConfig.getSecretKey().getBytes());
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid or expired token.");
        }
    }

    public String getUserIdFromToken(String token) {
        return validateToken(token).getSubject();
    }

    public Date getExpiration(String token) {
        return validateToken(token).getExpiration();
    }
}
