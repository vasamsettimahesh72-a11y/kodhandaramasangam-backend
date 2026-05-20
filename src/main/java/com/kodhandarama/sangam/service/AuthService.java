package com.kodhandarama.sangam.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kodhandarama.sangam.dto.LoginResponse;
import com.kodhandarama.sangam.dto.RegisterRequest;
import com.kodhandarama.sangam.entity.Member;
import com.kodhandarama.sangam.entity.OtpStore;
import com.kodhandarama.sangam.repository.MemberRepository;
import com.kodhandarama.sangam.repository.OtpStoreRepository;
import com.kodhandarama.sangam.security.JwtUtil;

@Service
public class AuthService {

    private final MemberRepository   memberRepository;
    private final OtpStoreRepository otpStoreRepository;
    private final PasswordEncoder    passwordEncoder;
    private final EmailService       emailService;
    private final JwtUtil            jwtUtil;

    private static final String ADMIN_PHONE    = "9346285157";
    private static final String ADMIN_NAME     = "Admin";
    private static final String ADMIN_PASSWORD = "Sangam@2024";

    public AuthService(MemberRepository memberRepository,
                       OtpStoreRepository otpStoreRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService,
                       JwtUtil jwtUtil) {
        this.memberRepository   = memberRepository;
        this.otpStoreRepository = otpStoreRepository;
        this.passwordEncoder    = passwordEncoder;
        this.emailService       = emailService;
        this.jwtUtil            = jwtUtil;
    }

    // ── Send OTP ─────────────────────────────────────────────────────────────

    @Transactional
    public String sendOtp(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already registered.");
        }

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        OtpStore store = otpStoreRepository.findByEmail(email)
                .orElse(new OtpStore());
        store.setEmail(email);
        store.setOtpHash(passwordEncoder.encode(otp));
        store.setExpiry(LocalDateTime.now().plusMinutes(5));
        store.setVerified(false);
        otpStoreRepository.save(store);

        try {
            emailService.sendOtp(email, otp);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP. Please check your email address.");
        }

        return "OTP sent to " + email;
    }

    // ── Verify OTP ───────────────────────────────────────────────────────────

    @Transactional
    public String verifyOtp(String email, String otp) {
        OtpStore store = otpStoreRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP not found. Please request a new one."));

        if (store.getExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired. Please request a new one.");
        }
        if (!passwordEncoder.matches(otp, store.getOtpHash())) {
            throw new RuntimeException("Invalid OTP. Please try again.");
        }

        store.setVerified(true);
        otpStoreRepository.save(store);
        return "Email verified successfully.";
    }

    // ── Register ─────────────────────────────────────────────────────────────

    @Transactional
    public String register(RegisterRequest request) {
        if (ADMIN_PHONE.equals(request.getPhoneNumber())) {
            throw new RuntimeException("This phone number is not allowed for registration.");
        }

        OtpStore store = otpStoreRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Please verify your email with OTP first."));

        if (!store.isVerified()) {
            throw new RuntimeException("Email not verified. Please complete OTP verification first.");
        }
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered.");
        }
        if (memberRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number is already registered.");
        }

        Member member = new Member();
        member.setName(request.getName());
        member.setEmail(request.getEmail());
        member.setPhoneNumber(request.getPhoneNumber());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setAddress(request.getAddress());
        member.setStatus(Member.Status.PENDING);
        member.setRole(Member.Role.MEMBER);
        member.setEmailVerified(true);
        memberRepository.save(member);

        otpStoreRepository.deleteByEmail(request.getEmail());

        return "Registration successful! Please wait for admin approval.";
    }

    // ── Member Login ─────────────────────────────────────────────────────────

    public LoginResponse memberLogin(String phoneNumber, String password) {
        if (ADMIN_PHONE.equals(phoneNumber)) {
            throw new RuntimeException("Please use the admin login page.");
        }

        Member member = memberRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Invalid phone number or password."));

        if (member.getStatus() == Member.Status.PENDING) {
            throw new RuntimeException("Your account is pending admin approval.");
        }
        if (member.getStatus() == Member.Status.REJECTED) {
            throw new RuntimeException("Your account has been rejected. Please contact admin.");
        }

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new RuntimeException("Invalid phone number or password.");
        }

        String token = jwtUtil.generateToken(phoneNumber, "MEMBER");
        return new LoginResponse(token, "MEMBER", member.getId(), member.getName());
    }

    // ── Admin Login ──────────────────────────────────────────────────────────

    public LoginResponse adminLogin(String phoneNumber, String password) {
        if (!ADMIN_PHONE.equals(phoneNumber)) {
            throw new RuntimeException("Invalid admin credentials.");
        }
        if (!ADMIN_PASSWORD.equals(password)) {
            throw new RuntimeException("Invalid admin credentials.");
        }

        String token = jwtUtil.generateToken(phoneNumber, "ADMIN");
        return new LoginResponse(token, "ADMIN", 0L, ADMIN_NAME);
    }
}