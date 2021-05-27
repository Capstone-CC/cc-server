package com.cau.cc.model.repository;

import com.cau.cc.model.entity.Matching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchingRepository extends JpaRepository<Matching, Long> {

    @Query("SELECT m FROM Matching m join fetch m.manId join fetch m.womanId WHERE m.manId.id = :mId and m.womanId.id = :wId and m.time = :time")
    Matching findByManIdAndWomanIdAndTime(@Param("mId") Long mId,@Param("wId") Long wId,@Param("time") LocalDateTime time);

    @Query("SELECT m from Matching m join fetch m.manId where m.manId.id = :mId")
    List<Matching> findByManId(@Param("mId") Long id);

    @Query("SELECT m from Matching m join fetch m.womanId where m.womanId.id = :wId")
    List<Matching> findByWomanId(@Param("wId") Long id);


}
