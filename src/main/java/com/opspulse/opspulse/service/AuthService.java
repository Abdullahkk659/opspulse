package com.opspulse.opspulse.service;
import com.opspulse.opspulse.dto.AuthResponse;
import com.opspulse.opspulse.dto.LoginRequest;
import com.opspulse.opspulse.entity.User;
import com.opspulse.opspulse.repository.UserRepository;
import com.opspulse.opspulse.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder,JwtUtil jwtUtil){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.jwtUtil=jwtUtil;
    }
    public AuthResponse login(LoginRequest request){
        User user=userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new RuntimeException("User does not exist!"));
        boolean hashPassword=passwordEncoder.matches(request.getPassword(),user.getPasswordHash());
        if(!hashPassword){
            throw new RuntimeException("Invalid email or password");
        }
        String token=jwtUtil.generateToken(user.getId(),user.getTenant().getId());
        return new AuthResponse(token);
    }
}
