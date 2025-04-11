package me.sarismart.backend.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class SupabaseAuthService {
    
    private final String SUPABASE_URL;
    private final String API_KEY;

   
    private RestTemplate restTemplate;

    public SupabaseAuthService(@Value("${SUPABASE_URL}") String SUPABASE_URL, @Value("${API_KEY}") String API_KEY) {
        this.SUPABASE_URL = SUPABASE_URL;
        this.API_KEY = API_KEY;
    }

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
