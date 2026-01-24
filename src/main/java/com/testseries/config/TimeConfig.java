package com.testseries.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class TimeConfig {

    @PostConstruct
    public void init() {
        // Force the application to use IST (Indian Standard Time)
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        System.out.println("✅ Application TimeZone set to Asia/Kolkata (IST): " + new java.util.Date());
    }
}
