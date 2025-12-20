package com.testseries.controller;

import com.testseries.model.Question;
import com.testseries.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*")
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long id, @RequestBody Question question) {
        question.setId(id);
        Question updated = questionRepository.save(question);
        return ResponseEntity.ok(updated);
    }
}
