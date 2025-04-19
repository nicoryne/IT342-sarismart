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

    public ResponseEntity<String> signUp(String email, String password) {
        String url = appConfig.getUrl() + "/auth/v1/signup"; // Ensure the URL is correct
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", appConfig.getApiKey());
    
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
    
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
    
        try {
            // Make the POST request to Supabase
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    
            // Log the status and the response body
            System.out.println("Supabase signUp response status: " + response.getStatusCode());
            System.out.println("Supabase signUp response body: " + response.getBody());
    
            // Check if the response is successful
            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                try {
                    JSONObject json = new JSONObject(response.getBody());
                    String supabaseUid = json.getJSONObject("user").getString("id");
                    String fullName = json.getJSONObject("user")
                            .optJSONObject("user_metadata")
                            .optString("full_name", "Unnamed");
    
                    // Save user to database
                    userService.saveUserToDatabase(email, supabaseUid, fullName);
                    
                    // Return success response
                    return ResponseEntity.status(HttpStatus.CREATED).body("User signed up successfully!");
    
                } catch (JSONException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing Supabase response: " + e.getMessage());
                }
            } else {
                // Handle non-successful status codes
                return ResponseEntity.status(response.getStatusCode()).body("Failed to sign up user: " + response.getBody());
            }
        } catch (Exception e) {
            // Catch any connection or other unexpected errors
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during sign up request: " + e.getMessage());
        }
    }    

    public ResponseEntity<String> signIn(String email, String password) {
        String url = appConfig.getUrl() + "/auth/v1/token?grant_type=password";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", appConfig.getApiKey());

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }
}
