package com.testseries.controller;

import com.testseries.dto.BookmarkDTO;
import com.testseries.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookmarks")
@CrossOrigin(origins = "*")
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    @GetMapping
    public ResponseEntity<List<BookmarkDTO>> getUserBookmarks(@RequestParam Long userId) {
        return ResponseEntity.ok(bookmarkService.getUserBookmarks(userId));
    }

    @PostMapping
    public ResponseEntity<BookmarkDTO> addBookmark(@RequestBody Map<String, Long> payload) {
        Long userId = payload.get("userId");
        Long questionId = payload.get("questionId");
        return ResponseEntity.ok(bookmarkService.addBookmark(userId, questionId));
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> removeBookmark(@RequestParam Long userId, @PathVariable Long questionId) {
        bookmarkService.removeBookmark(userId, questionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> isBookmarked(@RequestParam Long userId, @RequestParam Long questionId) {
        return ResponseEntity.ok(bookmarkService.isBookmarked(userId, questionId));
    }
}
