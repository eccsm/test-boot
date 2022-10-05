package com.eccsm.testBootApplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eccsm.testBootApplication.model.Grade;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
	
	List<Grade> findByStudentId(Long studentId);

	List<Grade> findByLectureAndStudentId(String lecture, Long studentId);
}
