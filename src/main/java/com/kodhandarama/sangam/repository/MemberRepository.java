package com.kodhandarama.sangam.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kodhandarama.sangam.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // ── Lookup ───────────────────────────────────────────────────────────────

    Optional<Member> findByEmail(String email);

    Optional<Member> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    // ── Status filters ───────────────────────────────────────────────────────

    List<Member> findByStatus(Member.Status status);

    long countByStatus(Member.Status status);

    // ── Financial aggregates ─────────────────────────────────────────────────

    @Query("SELECT COALESCE(SUM(m.totalPaid), 0) FROM Member m WHERE m.status = :status")
    Double sumTotalPaidApproved(@Param("status") Member.Status status);

    @Query("SELECT COALESCE(SUM(m.totalPending), 0) FROM Member m WHERE m.status = :status")
    Double sumTotalPendingApproved(@Param("status") Member.Status status);
}