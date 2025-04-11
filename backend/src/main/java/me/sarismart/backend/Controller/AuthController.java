package me.sarismart.backend.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import me.sarismart.backend.DTO.AuthRequest;
import me.sarismart.backend.Service.SupabaseAuthService;

@RestController
@RequestMapping("api/v1/auth")
@Tag(name = "AuthController", description = "Authentication and Authorization")
public class AuthController {
    @Autowired
    private SupabaseAuthService supabaseAuthService;

    @Operation(summary = "Sign Up", description = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<String> signUp(@RequestBody AuthRequest authRequest) {
        return supabaseAuthService.signUp(authRequest.getEmail(), authRequest.getPassword());
    }

    @Operation(summary = "Sign In", description = "Authenticate a user")
    @PostMapping("/login")
    public ResponseEntity<String> signIn(@RequestBody AuthRequest authRequest) {
        return supabaseAuthService.signIn(authRequest.getEmail(), authRequest.getPassword());
    }
}
