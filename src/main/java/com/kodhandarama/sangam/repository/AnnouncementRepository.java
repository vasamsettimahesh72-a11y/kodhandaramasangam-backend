package com.kodhandarama.sangam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kodhandarama.sangam.entity.Announcement;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findAllByOrderByCreatedAtDesc();
}