package br.com.couponapi.config;


import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@TestConfiguration
public class TimeConfigTest {

    @Bean
    public Clock clock() {
        return Clock.fixed(
                Instant.parse("2025-01-01T12:00:00Z"),
                ZoneId.of("America/Sao_Paulo")
        );
    }
}