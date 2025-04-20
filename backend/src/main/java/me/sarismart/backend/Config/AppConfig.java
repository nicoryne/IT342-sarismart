package me.sarismart.backend.Config;

import org.springframework.stereotype.Component;

@Component
public class AppConfig {

    private final String url = System.getenv("SUPABASE_URL");
    private final String apiKey = System.getenv("SUPABASE_API_KEY");
    private final String secretKey = System.getenv("JWT_SECRET_KEY");

    public String getUrl() {
        return url;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }
}