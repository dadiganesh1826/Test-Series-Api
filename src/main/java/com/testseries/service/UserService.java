package com.testseries.service;

import com.testseries.dto.LoginRequest;
import com.testseries.dto.RegisterRequest;
import com.testseries.dto.UserResponse;
import com.testseries.model.User;
import com.testseries.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // In production, hash the password!

        user = userRepository.save(user);

        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    @Transactional
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        updateStreak(user);

        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public User updateProfile(Long userId, User updatedUser) {
        User user = getUserById(userId);
        if (updatedUser.getBio() != null) user.setBio(updatedUser.getBio());
        if (updatedUser.getPhoneNumber() != null) user.setPhoneNumber(updatedUser.getPhoneNumber());
        if (updatedUser.getTargetExam() != null) user.setTargetExam(updatedUser.getTargetExam());
        if (updatedUser.getProfilePicture() != null) user.setProfilePicture(updatedUser.getProfilePicture());
        return userRepository.save(user);
    }

    @Transactional
    public User updateGoal(Long userId, Integer weeklyGoal) {
        User user = getUserById(userId);
        user.setWeeklyGoal(weeklyGoal);
        return userRepository.save(user);
    }

    private void updateStreak(User user) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime lastLogin = user.getLastLoginDate();

        if (lastLogin != null) {
            java.time.LocalDate lastDate = lastLogin.toLocalDate();
            java.time.LocalDate today = now.toLocalDate();

            if (lastDate.equals(today.minusDays(1))) {
                // Consecutive day
                user.setCurrentStreak((user.getCurrentStreak() == null ? 0 : user.getCurrentStreak()) + 1);
            } else if (lastDate.isBefore(today)) {
                // Missed a day or more (but allow multiple logins in same day)
                user.setCurrentStreak(1);
            }
        } else {
            // First time login
            user.setCurrentStreak(1);
        }

        if (user.getCurrentStreak() > (user.getLongestStreak() == null ? 0 : user.getLongestStreak())) {
            user.setLongestStreak(user.getCurrentStreak());
        }

        user.setLastLoginDate(now);
        userRepository.save(user);
    }
    @Transactional
    public void updateLastActivity(Long userId) {
        // Optimization: Use a custom query to avoid fetching the whole entity if possible,
        // but for now, fetching is fine as it's not high frequency (every 5 mins).
        User user = getUserById(userId);
        user.setLastActivityDate(java.time.LocalDateTime.now());
        userRepository.save(user);
    }
}
