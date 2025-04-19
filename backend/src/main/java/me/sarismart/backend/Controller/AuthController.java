package me.sarismart.backend.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
