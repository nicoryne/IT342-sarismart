package me.sarismart.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class SupabaseAuthService {
    private final String SUPABASE_URL = "https://mpvwyygeoesopxralxxl.supabase.co";
    private final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1wdnd5eWdlb2Vzb3B4cmFseHhsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM3NDg2OTYsImV4cCI6MjA1OTMyNDY5Nn0.db_Loy9LCRmmMxMYgX2iAGvqvOnxD_34jrQzIbnlw9Q";

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<String> signUp(String email, String password) {
        String url = SUPABASE_URL + "/auth/v1/signup";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", API_KEY);

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    public ResponseEntity<String> signIn(String email, String password) {
        String url = SUPABASE_URL + "/auth/v1/token?grant_type=password";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", API_KEY);

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }
}
