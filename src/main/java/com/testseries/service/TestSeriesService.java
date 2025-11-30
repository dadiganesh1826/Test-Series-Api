package com.testseries.service;

import com.testseries.model.Question;
import com.testseries.model.TestSeries;
import com.testseries.repository.QuestionRepository;
import com.testseries.repository.TestSeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestSeriesService {
    private final TestSeriesRepository testSeriesRepository;
    private final QuestionRepository questionRepository;

    public List<TestSeries> getAllActiveTestSeries() {
        return testSeriesRepository.findByIsActiveTrue();
    }

    public TestSeries getTestSeriesById(Long id) {
        return testSeriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test series not found"));
    }

    public List<Question> getQuestionsByTestSeriesId(Long testSeriesId) {
        return questionRepository.findByTestSeriesId(testSeriesId);
    }

    @Transactional
    public TestSeries createTestSeries(TestSeries testSeries) {
        return testSeriesRepository.save(testSeries);
    }

    @Transactional
    public Question addQuestion(Long testSeriesId, Question question) {
        TestSeries testSeries = getTestSeriesById(testSeriesId);
        question.setTestSeries(testSeries);
        return questionRepository.save(question);
    }
}
