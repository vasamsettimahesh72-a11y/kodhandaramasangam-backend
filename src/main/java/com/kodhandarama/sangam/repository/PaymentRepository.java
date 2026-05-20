package com.kodhandarama.sangam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kodhandarama.sangam.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByMemberId(Long memberId);

    List<Payment> findAllByOrderByPaymentDateDesc();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.member.id = :memberId " +
           "AND p.paymentStatus = :status " +
           "AND SUBSTRING(CAST(p.paymentDate AS string), 1, 7) = :month")
    Double getMemberPaidByMonth(@Param("memberId") Long memberId,
                                @Param("status")   Payment.PaymentStatus status,
                                @Param("month")    String month);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.paymentStatus = :status " +
           "AND SUBSTRING(CAST(p.paymentDate AS string), 1, 7) = :month")
    Double getTotalByStatusAndMonth(@Param("status") Payment.PaymentStatus status,
                                    @Param("month")  String month);
}
