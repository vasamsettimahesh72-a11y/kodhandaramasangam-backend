package com.kodhandarama.sangam.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kodhandarama.sangam.dto.LoginRequest;
import com.kodhandarama.sangam.dto.LoginResponse;
import com.kodhandarama.sangam.dto.OtpRequest;
import com.kodhandarama.sangam.dto.RegisterRequest;
import com.kodhandarama.sangam.service.AuthService;
import com.kodhandarama.sangam.service.EmailService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService  authService;
    private final EmailService emailService;

    public AuthController(AuthService authService, EmailService emailService) {
        this.authService  = authService;
        this.emailService = emailService;
    }

    // ── Send OTP ──────────────────────────────────────────────────

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(
                Map.of("message", authService.sendOtp(body.get("email")))
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Verify OTP ────────────────────────────────────────────────

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest req) {
        try {
            return ResponseEntity.ok(
                Map.of("message", authService.verifyOtp(req.getEmail(), req.getOtp()))
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Register ──────────────────────────────────────────────────

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            return ResponseEntity.ok(
                Map.of("message", authService.register(req))
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Member Login ──────────────────────────────────────────────

    @PostMapping("/member-login")
    public ResponseEntity<?> memberLogin(@RequestBody LoginRequest req) {
        try {
            LoginResponse res = authService.memberLogin(req.getPhoneNumber(), req.getPassword());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Admin Login ───────────────────────────────────────────────

    @PostMapping("/admin-login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest req) {
        try {
            LoginResponse res = authService.adminLogin(req.getPhoneNumber(), req.getPassword());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Contact Form ──────────────────────────────────────────────

    @PostMapping("/contact")
    public ResponseEntity<?> contactAdmin(@RequestBody Map<String, String> body) {
        try {
            String name    = body.getOrDefault("name",    "Unknown");
            String email   = body.getOrDefault("email",   "");
            String phone   = body.getOrDefault("phone",   "");
            String message = body.getOrDefault("message", "");

            if (message.isBlank()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Message cannot be empty."));
            }

            emailService.sendContactMessageToAdmin(name, email, phone, message);
            return ResponseEntity.ok(Map.of("message", "Message sent to admin successfully!"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to send message: " + e.getMessage()));
        }
    }
}