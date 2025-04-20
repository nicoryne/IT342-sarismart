package me.sarismart.backend.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import me.sarismart.backend.Config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final AppConfig appConfig;

    @Autowired
    public JwtUtil(AppConfig appConfig) {
        this.appConfig = appConfig;
        System.out.println("AppConfig URL: " + appConfig.getUrl());
        System.out.println("AppConfig API Key: " + appConfig.getApiKey()); 
        System.out.println("AppConfig Secret Key: " + appConfig.getSecretKey());
    }

    public Claims validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(appConfig.getSecretKey().getBytes());
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            System.out.println("Claims: " + claims);

            // Validate issuer (iss)
            String expectedIssuer = appConfig.getUrl() + "/auth/v1";
            if (!expectedIssuer.equals(claims.getIssuer())) {
                System.out.println("Invalid issuer: " + claims.getIssuer());
                throw new RuntimeException("Invalid issuer");
            }
    
            // Validate audience (aud)
            String expectedAudience = "authenticated";
            if (!expectedAudience.equals(claims.getAudience())) {
                System.out.println("Invalid audience: " + claims.getAudience());
                throw new RuntimeException("Invalid audience");
            }
    
            // Validate expiration (exp)
            if (claims.getExpiration().before(new Date())) {
                System.out.println("Token has expired: " + claims.getExpiration());
                throw new RuntimeException("Token has expired");
            }
    
            // Validate subject (sub)
            if (claims.getSubject() == null || claims.getSubject().isEmpty()) {
                System.out.println("Invalid or missing subject claim");
                throw new RuntimeException("Invalid or missing subject claim");
            }
    
            System.out.println("Token is valid");
            return claims;
        } catch (JwtException e) {
            System.out.println("JWT Exception: " + e.getMessage());
            throw new RuntimeException("Invalid or expired token.");
        }
    }

    public String getUserIdFromToken(String token) {
        return validateToken(token).getSubject();
    }
}