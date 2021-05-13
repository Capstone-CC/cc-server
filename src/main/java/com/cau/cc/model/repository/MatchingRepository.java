package com.cau.cc.model.repository;

import com.cau.cc.model.entity.Matching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MatchingRepository extends JpaRepository<Matching, Long> {

    @Query("SELECT m FROM Matching m  WHERE m.manId.id = :mId and m.womanId.id = :wId and m.time = :time")
    Matching findByManIdAndWomanIdAndTime(@Param("mId") Long mId,@Param("wId") Long wId,@Param("time") LocalDateTime time);
}
