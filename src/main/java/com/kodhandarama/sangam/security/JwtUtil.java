package com.kodhandarama.sangam.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final String SECRET        = "KodhandaramaSangamSecretKeyForJWTTokenGenerationMustBe256Bits!";
    private static final long   EXPIRATION_MS = 86_400_000L;

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // ── Generate Token ────────────────────────────────────────────

    public String generateToken(String phoneNumber, String role) {
        return Jwts.builder()
                .subject(phoneNumber)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    // ── Extract Phone Number ──────────────────────────────────────

    public String extractPhone(String token) {
        return getClaims(token).getSubject();
    }

    // ── Extract Role ──────────────────────────────────────────────

    public String extractRole(String token) {
        return (String) getClaims(token).get("role");
    }

    // ── Validate Token ────────────────────────────────────────────

    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ── Parse Claims ──────────────────────────────────────────────

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}