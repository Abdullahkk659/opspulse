package com.opspulse.opspulse.controller;
import com.opspulse.opspulse.dto.AuthResponse;
import com.opspulse.opspulse.dto.LoginRequest;
import com.opspulse.opspulse.service.AuthService;
import jakarta.validation.Valid;
import  org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService){
        this.authService=authService;
    }
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request){return authService.login(request);}
}
