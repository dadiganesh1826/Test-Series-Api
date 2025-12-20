package com.testseries.config;

import com.testseries.service.GamificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GamificationInitializer implements CommandLineRunner {

    private final GamificationService gamificationService;

    @Override
    public void run(String... args) {
        gamificationService.initBadges();
        System.out.println("Gamification badges initialized.");
    }
}
