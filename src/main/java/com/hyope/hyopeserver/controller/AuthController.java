package com.hyope.hyopeserver.controller;

import com.hyope.hyopeserver.dto.guardian.GuardianResponse;
import com.hyope.hyopeserver.dto.guardian.auth.LoginResponse;
import com.hyope.hyopeserver.dto.guardian.auth.LoginRequest;
import com.hyope.hyopeserver.dto.guardian.SignupRequest;
import com.hyope.hyopeserver.entity.Guardian;
import com.hyope.hyopeserver.service.GuardianService;
import com.hyope.hyopeserver.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final GuardianService guardianService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<GuardianResponse> register(@RequestBody SignupRequest request) {
        Guardian created = guardianService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GuardianResponse.createGuardianResponseDto(created));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Guardian guardian = guardianService.login(request.getLoginId(), request.getPassword());
        String token = jwtUtil.generateToken(guardian.getLoginId(), guardian.getRole());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
