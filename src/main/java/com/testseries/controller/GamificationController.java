package com.testseries.controller;

import com.testseries.model.Badge;
import com.testseries.model.UserBadge;
import com.testseries.service.GamificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gamification")
@CrossOrigin(origins = "*")
public class GamificationController {

    @Autowired
    private GamificationService gamificationService;

    @GetMapping("/badges/{userId}")
    public ResponseEntity<List<UserBadge>> getUserBadges(@PathVariable Long userId) {
        return ResponseEntity.ok(gamificationService.getUserBadges(userId));
    }
    
    @GetMapping("/badges/all")
    public ResponseEntity<List<Badge>> getAllBadges() {
        return ResponseEntity.ok(gamificationService.getAllBadges());
    }

    // Endpoint to manually trigger badge check (e.g. after a test)
    // In a real app, this would be an event listener or called from ExamService
    @PostMapping("/check/{userId}")
    public ResponseEntity<Void> checkBadges(@PathVariable Long userId) {
        gamificationService.checkForBadges(userId);
        return ResponseEntity.ok().build();
    }
}
