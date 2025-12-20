package com.testseries.service;

import com.testseries.dto.NoteDTO;
import com.testseries.model.Note;
import com.testseries.model.Question;
import com.testseries.model.User;
import com.testseries.repository.NoteRepository;
import com.testseries.repository.QuestionRepository;
import com.testseries.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public NoteDTO saveNote(Long userId, Long questionId, String content) {
        Optional<Note> existingNote = noteRepository.findByUserIdAndQuestionId(userId, questionId);

        Note note;
        if (existingNote.isPresent()) {
            note = existingNote.get();
            note.setContent(content);
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            note = new Note();
            note.setUser(user);
            note.setQuestion(question);
            note.setContent(content);
        }

        Note savedNote = noteRepository.save(note);
        return convertToDTO(savedNote);
    }

    public NoteDTO getNote(Long userId, Long questionId) {
        return noteRepository.findByUserIdAndQuestionId(userId, questionId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public void deleteNote(Long userId, Long questionId) {
        noteRepository.deleteByUserIdAndQuestionId(userId, questionId);
    }

    private NoteDTO convertToDTO(Note note) {
        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setUserId(note.getUser().getId());
        dto.setQuestionId(note.getQuestion().getId());
        dto.setContent(note.getContent());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());
        return dto;
    }
}
