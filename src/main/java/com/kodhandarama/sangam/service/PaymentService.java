package com.kodhandarama.sangam.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kodhandarama.sangam.dto.PaymentRequest;
import com.kodhandarama.sangam.entity.Member;
import com.kodhandarama.sangam.entity.Payment;
import com.kodhandarama.sangam.repository.MemberRepository;
import com.kodhandarama.sangam.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberRepository  memberRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          MemberRepository memberRepository) {
        this.paymentRepository = paymentRepository;
        this.memberRepository  = memberRepository;
    }

    @Transactional
    public Payment recordPayment(PaymentRequest req) {
        Member member = memberRepository.findById(req.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found: " + req.getMemberId()));

        Payment.Operation     op     = req.getOperation();
        Payment.PaymentStatus status = req.getPaymentStatus();
        double                amount = req.getAmount();

        if (op == Payment.Operation.ADD) {
            if (status == Payment.PaymentStatus.PAID) {
                member.setTotalPaid(member.getTotalPaid() + amount);
            } else {
                member.setTotalPending(member.getTotalPending() + amount);
            }
        } else {
            if (status == Payment.PaymentStatus.PAID) {
                member.setTotalPaid(Math.max(0, member.getTotalPaid() - amount));
            } else {
                member.setTotalPending(Math.max(0, member.getTotalPending() - amount));
            }
        }

        memberRepository.save(member);

        Payment payment = new Payment();
        payment.setMember(member);
        payment.setAmount(amount);
        payment.setPaymentStatus(status);
        payment.setOperation(op);
        payment.setNote(req.getNote());
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByMember(Long memberId) {
        return paymentRepository.findByMemberId(memberId);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAllByOrderByPaymentDateDesc();
    }
}