package com.eccsm.testBootApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eccsm.testBootApplication.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

	Student findByEmail(String email);
}
