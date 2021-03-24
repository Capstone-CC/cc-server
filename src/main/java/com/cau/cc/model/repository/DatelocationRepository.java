package com.cau.cc.model.repository;

import com.cau.cc.model.entity.Datelocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatelocationRepository extends JpaRepository<Datelocation, Long> {
}
