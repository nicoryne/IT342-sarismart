package me.sarismart.backend.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import me.sarismart.backend.DTO.AuthRequest;
import me.sarismart.backend.Service.SupabaseAuthService;

@RestController
@RequestMapping("api/v1/auth")
@Tag(name = "AuthController", description = "Authentication and Authorization")
public class AuthController {
    @Autowired
    private SupabaseAuthService supabaseAuthService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API is working!");
    }

    @Operation(summary = "Sign Up", description = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<Object> signUp(@RequestBody AuthRequest authRequest) {
        System.out.println("Registering user: " + authRequest.getEmail());
        try {
            return supabaseAuthService.signUp(authRequest.getEmail(), authRequest.getPassword(), 
                                               authRequest.getPhone(), authRequest.getFullName());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Registration failed: " + e.getMessage());
        }
    }

    @Operation(summary = "Sign In", description = "Authenticate a user")
    @PostMapping("/login")
    public ResponseEntity<Object> signIn(@RequestBody AuthRequest authRequest) {
        return supabaseAuthService.signIn(authRequest.getEmail(), authRequest.getPassword());
    }

    @Operation(summary = "Refresh Session", description = "Refresh the current session using a refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshSession(@RequestBody Map<String, String> requestBody) {
        try {
            String refreshToken = requestBody.get("refresh_token");
            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Missing refresh_token in request body"));
            }

            return supabaseAuthService.refreshSession(refreshToken);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to refresh session", "details", e.getMessage()));
        }
    }

    @Operation(summary = "Get User Details", description = "Retrieve the current user details from Supabase")
    @GetMapping("/user")
    public ResponseEntity<Object> getUserDetails(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (!authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid Authorization header format"));
            }
            String accessToken = authorizationHeader.substring(7);

            return supabaseAuthService.getUserDetails(accessToken);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve user details", "details", e.getMessage()));
        }
    }
}
