package com.kodhandarama.sangam.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kodhandarama.sangam.entity.Member;
import com.kodhandarama.sangam.repository.MemberRepository;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public List<Member> getApprovedMembers() {
        return memberRepository.findByStatus(Member.Status.APPROVED);
    }

    public List<Member> getPendingMembers() {
        return memberRepository.findByStatus(Member.Status.PENDING);
    }

    public Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found: " + id));
    }

    public Member getProfile(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Member not found: " + phoneNumber));
    }

    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    @Transactional
    public Member approveMember(Long id) {
        Member member = getMember(id);
        member.setStatus(Member.Status.APPROVED);
        return memberRepository.save(member);
    }

    @Transactional
    public Member rejectMember(Long id) {
        Member member = getMember(id);
        member.setStatus(Member.Status.REJECTED);
        return memberRepository.save(member);
    }
}