package com.testseries.service;

import com.testseries.model.Badge;
import com.testseries.model.User;
import com.testseries.model.UserBadge;
import com.testseries.repository.BadgeRepository;
import com.testseries.repository.ExamAttemptRepository;
import com.testseries.repository.UserBadgeRepository;
import com.testseries.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GamificationService {

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    @Autowired
    private ExamAttemptRepository examAttemptRepository;
    
    @Autowired
    private UserRepository userRepository;

    public List<UserBadge> getUserBadges(Long userId) {
        return userBadgeRepository.findByUserId(userId);
    }
    
    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    @Transactional
    public void checkForBadges(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<Badge> allBadges = badgeRepository.findAll();
        
        // Fetch stats needed for checking
        // 1. Total finished exams
        int totalExams = examAttemptRepository.findByUserIdAndIsCompletedTrue(userId).size();
        
        // 2. Max score (simplified check for now)
        // You could add more complex logic here (e.g. 5 exams with > 90%)

        for (Badge badge : allBadges) {
            boolean earned = false;
            
            if ("TESTS_COMPLETED".equals(badge.getCriteriaType())) {
                if (totalExams >= badge.getCriteriaValue()) {
                    earned = true;
                }
            }
            else if ("STREAK".equals(badge.getCriteriaType())) {
                if (user.getCurrentStreak() != null && user.getCurrentStreak() >= badge.getCriteriaValue()) {
                    earned = true;
                }
            }

            if (earned) {
                awardBadgeIfNotExists(user, badge);
            }
        }
    }

    private void awardBadgeIfNotExists(User user, Badge badge) {
        if (!userBadgeRepository.existsByUserIdAndBadgeId(user.getId(), badge.getId())) {
            UserBadge userBadge = new UserBadge();
            userBadge.setUser(user);
            userBadge.setBadge(badge);
            userBadgeRepository.save(userBadge);
            System.out.println("Awarded badge: " + badge.getName() + " to user: " + user.getEmail());
        }
    }
    
    @Transactional
    public void initBadges() {
        createBadge("Novice", "Completed your first test!", "🌱", "TESTS_COMPLETED", 1);
        createBadge("Apprentice", "Completed 5 tests", "🔨", "TESTS_COMPLETED", 5);
        createBadge("Scholar", "Completed 10 tests", "📚", "TESTS_COMPLETED", 10);
        createBadge("Master", "Completed 25 tests", "🎓", "TESTS_COMPLETED", 25);
        createBadge("Legend", "Completed 50 tests", "👑", "TESTS_COMPLETED", 50);

        createBadge("Consistent", "3 Day Streak", "🔥", "STREAK", 3);
        createBadge("Dedicated", "7 Day Streak", "⚡", "STREAK", 7);
        createBadge("Unstoppable", "30 Day Streak", "🚀", "STREAK", 30);
    }

    private void createBadge(String name, String desc, String icon, String type, int value) {
        if (badgeRepository.findByName(name) == null) {
            Badge b = new Badge();
            b.setName(name);
            b.setDescription(desc);
            b.setIcon(icon);
            b.setCriteriaType(type);
            b.setCriteriaValue(value);
            badgeRepository.save(b);
        }
    }
}
