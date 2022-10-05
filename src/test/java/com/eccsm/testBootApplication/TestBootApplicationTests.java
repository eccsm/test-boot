package com.eccsm.testBootApplication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.eccsm.testBootApplication.model.Student;
import com.eccsm.testBootApplication.repository.StudentRepository;
import com.eccsm.testBootApplication.service.StudentService;

@TestPropertySource("/application-test.properties")
@SpringBootTest
class TestBootApplicationTests {


	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private StudentService studentService;

	@Test
	@DisplayName("Test Creating Student")
	@Order(2)
	public void createStudent() {
		Student student = new Student("Faruk", "Ilgaz", "faruk@ilgaz.com");

		studentService.createStudent(student);

		Student foundStudent = studentRepository.findByEmail("faruk@ilgaz.com");

		assertEquals("faruk@ilgaz.com", foundStudent.getEmail());

	}

	@Test
	@DisplayName("Student Null Check")
	@Order(1)
	public void nullCheck() {
		assertFalse(studentService.isStudentNull(1L));

		assertTrue(studentService.isStudentNull(0L));
	}

	@Test
	@DisplayName("Student Delete")
	@Order(3)
	public void deleteStudent() {

		assertFalse(studentService.isStudentNull(1L));

		studentService.deleteStudent(1L);

		assertTrue(studentService.isStudentNull(1L));

	}

	@Test
	@DisplayName("Get Students")
	@Order(0)
	public void getAllStudents() {

		List<Student> studentList = studentService.getStudents();

		assertFalse(studentList.isEmpty());

	}

}
