package com.testseries.controller;

import com.testseries.dto.AdminLoginRequest;
import com.testseries.dto.AdminLoginResponse;
import com.testseries.model.User;
import com.testseries.repository.UserRepository;
import com.testseries.repository.TestSeriesRepository;
import com.testseries.repository.ExamAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestSeriesRepository testSeriesRepository;

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @Autowired
    private com.testseries.repository.AdminActivityLogRepository activityLogRepository;

    private void logActivity(String email, String action, String description) {
        com.testseries.model.AdminActivityLog log = new com.testseries.model.AdminActivityLog();
        log.setAdminEmail(email);
        log.setAction(action);
        log.setDescription(description);
        activityLogRepository.save(log);
    }

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> adminLogin(@RequestBody AdminLoginRequest request) {
        // Authenticate against database
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Check password, Role, and Block status
            if (user.getPassword().equals(request.getPassword()) && 
               (user.getIsBlocked() == null || !user.getIsBlocked()) &&
               (user.getRole() == com.testseries.model.Role.SUPER_ADMIN || 
                user.getRole() == com.testseries.model.Role.CONTENT_ADMIN ||
                user.getRole() == com.testseries.model.Role.REVIEWER || 
                user.getRole() == com.testseries.model.Role.SUPPORT_ADMIN)) {
                
                logActivity(user.getEmail(), "LOGIN", "Admin logged in");
                
                AdminLoginResponse response = new AdminLoginResponse();
                response.setSuccess(true);
                response.setToken("admin-token-" + UUID.randomUUID().toString());
                
                Map<String, String> admin = new HashMap<>();
                admin.put("email", user.getEmail());
                admin.put("name", user.getName());
                admin.put("role", user.getRole().toString());
                response.setAdmin(admin);
                
                return ResponseEntity.ok(response);
            }
        }
        
        // Fallback for initial setup if no DB admin exists but credentials match default
        if ("admin@test.com".equals(request.getEmail()) && "admin123".equals(request.getPassword())) {
             // Create this admin in DB if not exists
             if (userOpt.isEmpty()) {
                 User admin = new User();
                 admin.setEmail("admin@test.com");
                 admin.setName("Super Admin");
                 admin.setPassword("admin123");
                 admin.setRole(com.testseries.model.Role.SUPER_ADMIN);
                 userRepository.save(admin);
             }
             
             logActivity("admin@test.com", "LOGIN", "Default Admin logged in");
             
             AdminLoginResponse response = new AdminLoginResponse();
             response.setSuccess(true);
             response.setToken("admin-token-" + UUID.randomUUID().toString());
             Map<String, String> admin = new HashMap<>();
             admin.put("email", "admin@test.com");
             admin.put("name", "Super Admin");
             admin.put("role", "SUPER_ADMIN");
             response.setAdmin(admin);
             return ResponseEntity.ok(response);
        }
        
        AdminLoginResponse response = new AdminLoginResponse();
        response.setSuccess(false);
        response.setMessage("Invalid credentials or unauthorized");
        return ResponseEntity.status(401).body(response);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalTestSeries", testSeriesRepository.count());
        stats.put("totalExams", examAttemptRepository.count());
        stats.put("activeUsers", userRepository.count()); // For now, same as total users
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users/count")
    public ResponseEntity<Long> getUserCount() {
        long count = userRepository.count();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/test-series/count")
    public ResponseEntity<Long> getTestSeriesCount() {
        long count = testSeriesRepository.count();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/exams/count")
    public ResponseEntity<Long> getExamCount() {
        long count = examAttemptRepository.count();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<Map<String, String>>> getRecentActivity() {
        List<Map<String, String>> activities = new ArrayList<>();
        List<com.testseries.model.AdminActivityLog> logs = activityLogRepository.findTop10ByOrderByTimestampDesc();
        
        for (com.testseries.model.AdminActivityLog log : logs) {
            Map<String, String> activity = new HashMap<>();
            activity.put("icon", "📝"); // Default icon
            if (log.getAction().contains("LOGIN")) activity.put("icon", "👤");
            if (log.getAction().contains("DELETE")) activity.put("icon", "🗑️");
            if (log.getAction().contains("BLOCK")) activity.put("icon", "🚫");
            
            activity.put("description", log.getDescription());
            activity.put("timestamp", log.getTimestamp().toString());
            activities.add(activity);
        }
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/analytics/user-growth")
    public ResponseEntity<List<Map<String, Object>>> getUserGrowth() {
        List<Map<String, Object>> growthData = new ArrayList<>();
        // In a real app, use a proper SQL Group By query. 
        // Simulating for now as we might not have much data
        java.time.LocalDate today = java.time.LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate date = today.minusDays(i);
            long count = userRepository.findAll().stream()
                .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().toLocalDate().equals(date))
                .count();
                
            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("date", date.toString());
            dataPoint.put("users", count);
            growthData.add(dataPoint);
        }
        return ResponseEntity.ok(growthData);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {
        try {
            userRepository.deleteById(userId);
            logActivity("admin", "DELETE_USER", "Deleted user with ID: " + userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to delete user");
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PutMapping("/users/{userId}/toggle-active")
    public ResponseEntity<Map<String, String>> toggleUserActive(@PathVariable Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // Toggle blocked status
                if (user.getIsBlocked() == null) {
                    user.setIsBlocked(true);
                } else {
                    user.setIsBlocked(!user.getIsBlocked());
                }
                userRepository.save(user);
                
                logActivity("admin", "BLOCK_USER", (user.getIsBlocked() ? "Blocked" : "Unblocked") + " user: " + user.getEmail());
                
                Map<String, String> response = new HashMap<>();
                response.put("message", user.getIsBlocked() ? "User blocked successfully" : "User unblocked successfully");
                response.put("isBlocked", user.getIsBlocked().toString());
                return ResponseEntity.ok(response);
            }
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to update user status");
            return ResponseEntity.status(500).body(response);
        }
    }
    @GetMapping("/test-series")
    public ResponseEntity<List<com.testseries.model.TestSeries>> getAllTestSeries() {
        return ResponseEntity.ok(testSeriesRepository.findAll());
    }

    @GetMapping("/analytics/test-performance")
    public ResponseEntity<List<Map<String, Object>>> getTestPerformance() {
        List<Map<String, Object>> performance = new ArrayList<>();
        List<Object[]> stats = examAttemptRepository.findTestPerformanceStats();
        
        for (Object[] row : stats) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", row[0]); // title
            data.put("attempts", row[1]); // count
            data.put("avgScore", row[2] != null ? Math.round((Double) row[2]) : 0); // avg
            performance.add(data);
        }
        
        return ResponseEntity.ok(performance);
    }
    
    @GetMapping("/analytics/top-scorers")
    public ResponseEntity<List<Map<String, Object>>> getTopScorers() {
        List<Map<String, Object>> leaderboard = new ArrayList<>();
        List<com.testseries.dto.LeaderboardEntryDTO> topScorers = examAttemptRepository.findGlobalLeaderboard();
        
        // Take top 5
        topScorers.stream().limit(5).forEach(dto -> {
             Map<String, Object> scorer = new HashMap<>();
             scorer.put("name", dto.getUserName());
             scorer.put("score", dto.getTotalScore());
             scorer.put("testsTaken", dto.getTestsTaken());
             leaderboard.add(scorer);
        });
        
        return ResponseEntity.ok(leaderboard);
    }

}
