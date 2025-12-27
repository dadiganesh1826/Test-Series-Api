package com.testseries.controller;

import com.testseries.model.Topic;
import com.testseries.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@CrossOrigin(origins = "*")
public class TopicController {

    @Autowired
    private TopicRepository topicRepository;

    @GetMapping
    public ResponseEntity<List<Topic>> getAllTopics() {
        return ResponseEntity.ok(topicRepository.findAll());
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<Topic>> getTopicsBySubject(@PathVariable Long subjectId) {
        return ResponseEntity.ok(topicRepository.findBySubjectId(subjectId));
    }

    @PostMapping
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic) {
        return ResponseEntity.ok(topicRepository.save(topic));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Topic> updateTopic(@PathVariable Long id, @RequestBody Topic topic) {
        topic.setId(id);
        return ResponseEntity.ok(topicRepository.save(topic));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        topicRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Autowired
    private com.testseries.repository.QuestionRepository questionRepository;

    @GetMapping("/{id}/questions")
    public ResponseEntity<List<com.testseries.model.Question>> getQuestionsByTopic(@PathVariable Long id) {
        return ResponseEntity.ok(questionRepository.findByTopicId(id));
    }
}
