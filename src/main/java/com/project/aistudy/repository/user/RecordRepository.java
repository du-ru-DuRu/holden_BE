package com.project.aistudy.repository.user;

import com.project.aistudy.entity.Record;
import com.project.aistudy.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByMemberAndToday(Member member, LocalDate today);
}