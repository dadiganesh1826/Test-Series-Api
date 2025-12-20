package com.testseries.service;

import com.testseries.dto.BookmarkDTO;
import com.testseries.model.Bookmark;
import com.testseries.model.Question;
import com.testseries.model.User;
import com.testseries.repository.BookmarkRepository;
import com.testseries.repository.QuestionRepository;
import com.testseries.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookmarkService {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public BookmarkDTO addBookmark(Long userId, Long questionId) {
        if (bookmarkRepository.existsByUserIdAndQuestionId(userId, questionId)) {
            throw new RuntimeException("Question already bookmarked");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setQuestion(question);

        Bookmark savedBookmark = bookmarkRepository.save(bookmark);
        return convertToDTO(savedBookmark);
    }

    public void removeBookmark(Long userId, Long questionId) {
        bookmarkRepository.deleteByUserIdAndQuestionId(userId, questionId);
    }

    public List<BookmarkDTO> getUserBookmarks(Long userId) {
        return bookmarkRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean isBookmarked(Long userId, Long questionId) {
        return bookmarkRepository.existsByUserIdAndQuestionId(userId, questionId);
    }

    private BookmarkDTO convertToDTO(Bookmark bookmark) {
        BookmarkDTO dto = new BookmarkDTO();
        dto.setId(bookmark.getId());
        dto.setUserId(bookmark.getUser().getId());
        dto.setQuestionId(bookmark.getQuestion().getId());
        dto.setQuestionText(bookmark.getQuestion().getQuestionText());
        // Handling potential nulls for subject/topic if they are not yet fully populated in DB
        if (bookmark.getQuestion().getSubject() != null) {
            dto.setSubject(bookmark.getQuestion().getSubject().getName());
        }
        if (bookmark.getQuestion().getTopic() != null) {
            dto.setTopic(bookmark.getQuestion().getTopic().getName());
        }
        dto.setCreatedAt(bookmark.getCreatedAt());
        return dto;
    }
}
