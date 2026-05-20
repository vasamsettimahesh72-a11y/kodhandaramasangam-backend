package com.kodhandarama.sangam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kodhandarama.sangam.entity.OtpStore;

public interface OtpStoreRepository extends JpaRepository<OtpStore, Long> {

    Optional<OtpStore> findByEmail(String email);

    void deleteByEmail(String email);
}