package com.testseries.repository;

import com.testseries.model.TestSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestSeriesRepository extends JpaRepository<TestSeries, Long> {
    List<TestSeries> findByIsActiveTrue();
}
