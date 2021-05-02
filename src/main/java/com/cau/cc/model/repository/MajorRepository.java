package com.cau.cc.model.repository;

import com.cau.cc.model.entity.Account;
import com.cau.cc.model.entity.Major;
import com.cau.cc.model.entity.MajorEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MajorRepository extends JpaRepository<Major, Long> {


    @Query("SELECT m FROM Major m join fetch m.accountList WHERE m.majorName = ?1")
    public Major findByMajorName(MajorEnum majorName);

}
