package com.testseries.controller;

import com.testseries.dto.NoteDTO;
import com.testseries.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @GetMapping("/question/{questionId}")
    public ResponseEntity<NoteDTO> getNote(@RequestParam Long userId, @PathVariable Long questionId) {
        NoteDTO note = noteService.getNote(userId, questionId);
        return ResponseEntity.ok(note);
    }

    @PostMapping
    public ResponseEntity<NoteDTO> saveNote(@RequestBody Map<String, Object> payload) {
        Long userId = ((Number) payload.get("userId")).longValue();
        Long questionId = ((Number) payload.get("questionId")).longValue();
        String content = (String) payload.get("content");
        return ResponseEntity.ok(noteService.saveNote(userId, questionId, content));
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteNote(@RequestParam Long userId, @PathVariable Long questionId) {
        noteService.deleteNote(userId, questionId);
        return ResponseEntity.ok().build();
    }
}
