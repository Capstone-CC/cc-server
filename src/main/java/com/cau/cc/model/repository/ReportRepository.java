package com.cau.cc.model.repository;

import com.cau.cc.model.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    @Query(nativeQuery = true, value = "select * from report u where u.reporter_id = :reporterId and u.reported_id = :reportedId")
    Optional<Report> findByReporter(Long reporterId, Long reportedId);
}