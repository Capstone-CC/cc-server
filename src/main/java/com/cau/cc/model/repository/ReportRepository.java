package com.cau.cc.model.repository;

import com.cau.cc.model.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    @Query("select u from Report u where u.reporterId = ?1")
    List<Report> findByReporter(Long id);
}