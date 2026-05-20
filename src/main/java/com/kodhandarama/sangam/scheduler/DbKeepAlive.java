package com.kodhandarama.sangam.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DbKeepAlive {

    private static final Logger log = LoggerFactory.getLogger(DbKeepAlive.class);

    private final JdbcTemplate jdbcTemplate;

    public DbKeepAlive(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Scheduled(fixedRate = 240_000)
    public void keepAlive() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            log.info("DB keep-alive ping successful");
        } catch (Exception e) {
            log.error("DB keep-alive failed: {}", e.getMessage());
        }
    }
}