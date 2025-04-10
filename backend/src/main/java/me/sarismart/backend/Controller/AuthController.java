package me.sarismart.backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.sarismart.backend.Entity.AuthRequest;
import me.sarismart.backend.Service.SupabaseAuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private SupabaseAuthService supabaseAuthService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody AuthRequest authRequest) {
        return supabaseAuthService.signUp(authRequest.getEmail(), authRequest.getPassword());
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@RequestBody AuthRequest authRequest) {
        return supabaseAuthService.signIn(authRequest.getEmail(), authRequest.getPassword());
    }
}
