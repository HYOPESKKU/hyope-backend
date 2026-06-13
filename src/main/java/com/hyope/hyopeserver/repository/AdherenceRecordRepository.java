package com.hyope.hyopeserver.repository;

import com.hyope.hyopeserver.entity.AdherenceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdherenceRecordRepository extends JpaRepository<AdherenceRecord, Long> {
    List<AdherenceRecord> findByAssessmentId(Long assessmentId);
}
