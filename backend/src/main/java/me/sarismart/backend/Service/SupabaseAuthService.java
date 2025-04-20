package me.sarismart.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import me.sarismart.backend.Config.AppConfig;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

@Service
public class SupabaseAuthService {
    private final AppConfig appConfig;

    @Autowired
    public SupabaseAuthService(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private UserService userService;

    public ResponseEntity<Object> signUp(String email, String password, String phone, String fullName) {
        String url = appConfig.getUrl() + "/auth/v1/signup";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", appConfig.getApiKey());

        Map<String, String> data = new HashMap<>();
        data.put("full_name", fullName);
        data.put("phone", phone);

        Map<String, Object> options = new HashMap<>();
        options.put("data", data);

        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        body.put("options", options);

        System.out.println("Request body: " + new JSONObject(body));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // Print status and body for debugging in Render
            System.out.println("Supabase signUp response status: " + response.getStatusCode());
            System.out.println("Supabase signUp response body: " + response.getBody());

            if (response.getStatusCode() != HttpStatus.OK && response.getStatusCode() != HttpStatus.CREATED) {
                return ResponseEntity.status(response.getStatusCode())
                        .body(Map.of("error", "Failed to sign up user", "details", response.getBody()));
            }

            try {
                JSONObject json = new JSONObject(response.getBody());
                String supabaseUid = json.getJSONObject("user").getString("id");

                userService.saveUserToDatabase(email, supabaseUid, fullName, phone);

                return ResponseEntity.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(json.toMap());

            } catch (JSONException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Error parsing Supabase response: " + e.getMessage()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error during sign up request", "details", e.getMessage()));
        }
    }

    public ResponseEntity<Object> signIn(String email, String password) {
        String url = appConfig.getUrl() + "/auth/v1/token?grant_type=password";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", appConfig.getApiKey());

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // Print status and body for debugging in Render
            System.out.println("Supabase signIn response status: " + response.getStatusCode());
            System.out.println("Supabase signIn response body: " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject json = new JSONObject(response.getBody());

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(json.toMap());
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body(Map.of("error", "Failed to sign in", "details", response.getBody()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Exception during sign in", "details", e.getMessage()));
        }
    }
}
