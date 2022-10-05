package com.eccsm.testBootApplication.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eccsm.testBootApplication.exception.StudentOrGradeErrorResponse;
import com.eccsm.testBootApplication.exception.StudentOrGradeNotFoundException;
import com.eccsm.testBootApplication.model.Grade;
import com.eccsm.testBootApplication.model.GradebookStudent;
import com.eccsm.testBootApplication.model.Student;
import com.eccsm.testBootApplication.service.StudentService;

@RestController
public class GradeBookController {

	@Autowired
	private StudentService studentservice;

	@GetMapping("/")
	public List<Student> getStudents() {
		return studentservice.getStudents();

	}

	@GetMapping("/studentInformation/{id}")
	public Student getStudentById(@PathVariable Long id) {
		if (studentservice.isStudentNull(id))
			throw new StudentOrGradeNotFoundException("Student or Grade was not found");

		return studentservice.studentInformation(id);

	}

	@PostMapping("/student")
	public List<Student> createStudent(@RequestBody Student student) {
		studentservice.createStudent(student);
		List<Student> students = studentservice.getStudents();
		return students;
	}

	@DeleteMapping("/student/{id}")
	public List<Student> deleteStudentById(@PathVariable Long id) {
		if (studentservice.isStudentNull(id))
			throw new StudentOrGradeNotFoundException("Student or Grade was not found");

		studentservice.deleteStudent(id);
		List<Student> students = studentservice.getStudents();
		return students;

	}

	@PostMapping("/grade")
	public GradebookStudent createGrade(@RequestBody Grade grade) {

		if (studentservice.isStudentNull(grade.getStudentId()))
			throw new StudentOrGradeNotFoundException("Student or Grade was not found");

		Grade created = studentservice.createGrade(grade);

		GradebookStudent studentEntity = studentservice.studentInformation(created.getStudentId());

		if (studentEntity == null)
			throw new StudentOrGradeNotFoundException("Student or Grade was not found");

		return studentEntity;

	}

	@GetMapping("/grades")
	public String getGrades(Model m) {
		List<Grade> grades = studentservice.getGrades();
		m.addAttribute("grades", grades);
		return "studentInformation";

	}

	@DeleteMapping("/grade/{id}")
	public List<Grade> deleteGrade(@PathVariable Long id, Model m) {
		if (studentservice.isGradeNull(id))
			throw new StudentOrGradeNotFoundException("Student or Grade was not found");

		studentservice.deleteGrade(id);
		List<Grade> grades = studentservice.getGrades();
		return grades;

	}

	@ExceptionHandler
	public ResponseEntity<StudentOrGradeErrorResponse> handleException(StudentOrGradeNotFoundException exc) {

		StudentOrGradeErrorResponse error = new StudentOrGradeErrorResponse();

		error.setStatus(HttpStatus.NOT_FOUND.value());
		error.setMessage(exc.getMessage());
		error.setTimeStamp(System.currentTimeMillis());

		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler
	public ResponseEntity<StudentOrGradeErrorResponse> handleException(Exception exc) {

		StudentOrGradeErrorResponse error = new StudentOrGradeErrorResponse();

		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setMessage(exc.getMessage());
		error.setTimeStamp(System.currentTimeMillis());

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

}
