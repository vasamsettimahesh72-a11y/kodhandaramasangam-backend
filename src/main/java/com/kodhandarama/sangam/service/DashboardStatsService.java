package com.kodhandarama.sangam.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kodhandarama.sangam.dto.StatusUpdateRequest;
import com.kodhandarama.sangam.entity.DashboardStats;
import com.kodhandarama.sangam.entity.Member;
import com.kodhandarama.sangam.repository.DashboardStatsRepository;
import com.kodhandarama.sangam.repository.MemberRepository;
import com.kodhandarama.sangam.repository.PaymentRepository;

@Service
public class DashboardStatsService {

    private final DashboardStatsRepository statsRepository;
    private final MemberRepository         memberRepository;
    private final PaymentRepository        paymentRepository;

    public DashboardStatsService(DashboardStatsRepository statsRepository,
                                 MemberRepository memberRepository,
                                 PaymentRepository paymentRepository) {
        this.statsRepository   = statsRepository;
        this.memberRepository  = memberRepository;
        this.paymentRepository = paymentRepository;
    }

    public DashboardStats getStats() {
        return statsRepository.findById(1L).orElseGet(() -> {
            DashboardStats s = new DashboardStats();
            return statsRepository.save(s);
        });
    }

    @Transactional
    public DashboardStats updateStatus(StatusUpdateRequest req) {
        DashboardStats stats = getStats();

        if (Boolean.TRUE.equals(req.getAutoCalculate())) {
            Double totalPaid   = memberRepository.sumTotalPaidApproved(Member.Status.APPROVED);
            Double totalUnpaid = memberRepository.sumTotalPendingApproved(Member.Status.APPROVED);
            double total = (totalPaid   != null ? totalPaid   : 0.0)
                         + (totalUnpaid != null ? totalUnpaid : 0.0);
            long pendingCount = memberRepository.countByStatus(Member.Status.PENDING);

            stats.setTotalPaid(totalPaid     != null ? totalPaid   : 0.0);
            stats.setTotalUnpaid(totalUnpaid != null ? totalUnpaid : 0.0);
            stats.setTotalCollection(total);
            stats.setTotalPendingMembers((int) pendingCount);
            stats.setAutoCalculated(true);
        } else {
            if (req.getTotalCollection()     != null) stats.setTotalCollection(req.getTotalCollection());
            if (req.getTotalPaid()           != null) stats.setTotalPaid(req.getTotalPaid());
            if (req.getTotalUnpaid()         != null) stats.setTotalUnpaid(req.getTotalUnpaid());
            if (req.getTotalPendingMembers() != null) stats.setTotalPendingMembers(req.getTotalPendingMembers());
            stats.setAutoCalculated(false);
        }

        return statsRepository.save(stats);
    }
}