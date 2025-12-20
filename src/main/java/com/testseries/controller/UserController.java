package com.testseries.controller;

import com.testseries.model.User;
import com.testseries.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateProfile(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateProfile(id, user));
    }

    @PostMapping("/{id}/goal")
    public ResponseEntity<User> updateGoal(@PathVariable Long id, @RequestBody Map<String, Integer> payload) {
        Integer weeklyGoal = payload.get("weeklyGoal");
        return ResponseEntity.ok(userService.updateGoal(id, weeklyGoal));
    }
}
