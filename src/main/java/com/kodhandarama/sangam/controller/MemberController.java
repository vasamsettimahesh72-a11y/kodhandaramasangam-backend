package com.kodhandarama.sangam.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kodhandarama.sangam.entity.Member;
import com.kodhandarama.sangam.entity.Payment;
import com.kodhandarama.sangam.repository.MemberRepository;
import com.kodhandarama.sangam.repository.PaymentRepository;
import com.kodhandarama.sangam.service.MemberService;

@RestController
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService     memberService;
    private final PaymentRepository paymentRepository;
    private final MemberRepository  memberRepository;

    public MemberController(MemberService memberService,
                            PaymentRepository paymentRepository,
                            MemberRepository memberRepository) {
        this.memberService     = memberService;
        this.paymentRepository = paymentRepository;
        this.memberRepository  = memberRepository;
    }

    // ── My Profile ────────────────────────────────────────────────

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal String phoneNumber) {
        try {
            return ResponseEntity.ok(memberService.getProfile(phoneNumber));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── My Payment History ────────────────────────────────────────

    @GetMapping("/payments")
    public ResponseEntity<?> getMyPayments(@AuthenticationPrincipal String phoneNumber) {
        try {
            Member member   = memberService.getProfile(phoneNumber);
            List<Payment> p = paymentRepository.findByMemberId(member.getId());
            return ResponseEntity.ok(p);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── All Approved Members ──────────────────────────────────────

    @GetMapping("/members")
    public ResponseEntity<?> getAllApprovedMembers() {
        try {
            List<Member> approved = memberRepository.findByStatus(Member.Status.APPROVED);
            List<Map<String, Object>> result = new ArrayList<>();

            for (Member m : approved) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id",      m.getId());
                row.put("name",    m.getName());
                row.put("paid",    m.getTotalPaid());
                row.put("pending", m.getTotalPending());
                row.put("contact", m.getPhoneNumber());
                result.add(row);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}