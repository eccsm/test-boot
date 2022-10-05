package com.eccsm.testBootApplication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;

import com.eccsm.testBootApplication.model.Grade;
import com.eccsm.testBootApplication.model.GradebookStudent;
import com.eccsm.testBootApplication.model.Student;
import com.eccsm.testBootApplication.repository.GradeRepository;
import com.eccsm.testBootApplication.repository.StudentRepository;
import com.eccsm.testBootApplication.service.StudentService;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
public class ControllerTest {

	private static MockHttpServletRequest request;

	@Autowired
	private JdbcTemplate jdbc;

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private StudentService studentCreateServiceMock;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private GradeRepository gradeRepository;

	@Autowired
	private StudentService studentService;

	@Value("${sql.scripts.create.student}")
	private String createStudentQuery;

	@Value("${sql.scripts.create.math.grade}")
	private String createMathGradeQuery;

	@Value("${sql.scripts.create.history.grade}")
	private String createHistoryGradeQuery;

	@Value("${sql.scripts.create.science.grade}")
	private String createScienceGradeQuery;

	@Value("${sql.scripts.delete.student}")
	private String deleteStudentQuery;

	@Value("${sql.scripts.delete.grade}")
	private String deleteGradesQuery;

	@BeforeAll
	public static void setup() {
		request = new MockHttpServletRequest();
		request.setParameter("firstname", "Ekincan");
		request.setParameter("lastname", "Casim");
		request.setParameter("email", "ekincan@casim.net");
	}

	@BeforeEach
	public void setupDatabase() {
		jdbc.execute(createStudentQuery);

		jdbc.execute(createMathGradeQuery);

		jdbc.execute(createScienceGradeQuery);

		jdbc.execute(createHistoryGradeQuery);
	}

	@Test
	public void getStudentHttpRequest() throws Exception {
		Student stdOne = new Student("Cafer", "Erol", "cafer@erol.com");
		Student stdTwo = new Student("Emir", "Ates", "emir@ates.com");

		List<Student> stdList = new ArrayList<>(Arrays.asList(stdOne, stdTwo));

		when(studentCreateServiceMock.getStudents()).thenReturn(stdList);

		assertIterableEquals(stdList, studentCreateServiceMock.getStudents());

		MvcResult mvcResult = mockMvc.perform(get("/students")).andExpect(status().isOk()).andReturn();

		ModelAndView mav = mvcResult.getModelAndView();

		ModelAndViewAssert.assertViewName(mav, "index");
	}

	@Test
	public void createStudentHttpRequest() throws Exception {
		Student stdOne = new Student("Cafer", "Erol", "cafer@erol.com");

		List<Student> stdList = new ArrayList<>(Arrays.asList(stdOne));

		when(studentCreateServiceMock.getStudents()).thenReturn(stdList);

		assertIterableEquals(stdList, studentCreateServiceMock.getStudents());

		MvcResult mvcResult = mockMvc.perform(post("/student").contentType(MediaType.APPLICATION_JSON)
				.param("firstname", request.getParameterValues("firstname"))
				.param("lastname", request.getParameterValues("lastname"))
				.param("email", request.getParameterValues("email"))).andExpect(status().isOk()).andReturn();

		ModelAndView mav = mvcResult.getModelAndView();

		ModelAndViewAssert.assertViewName(mav, "index");

		Student createdStudent = studentRepository.findByEmail("ekincan@casim.net");

		assertNotNull(createdStudent);
	}

	@Test
	public void deleteStudentHttpRequest() throws Exception {

		assertTrue(studentRepository.findById(1L).isPresent());

		MvcResult mvcResult = mockMvc.perform(get("/student/{id}", 1L)).andExpect(status().isOk()).andReturn();

		ModelAndView mav = mvcResult.getModelAndView();

		ModelAndViewAssert.assertViewName(mav, "index");

		assertFalse(studentRepository.findById(1L).isPresent());
	}

	@Test
	public void deleteStudentHttpRequestErrorPage() throws Exception {

		MvcResult mvcResult = mockMvc.perform(get("/student/{id}", 0L)).andExpect(status().isOk()).andReturn();

		ModelAndView mav = mvcResult.getModelAndView();

		ModelAndViewAssert.assertViewName(mav, "error");

	}

	@Test
	public void createGradeService() throws Exception {
		Grade grade = new Grade();
		grade.setGrade(new BigDecimal(80.5));
		grade.setStudentId(1L);
		grade.setLecture("math");

		assertNotNull(studentService.createGrade(grade));

		List<Grade> grades = gradeRepository.findByLectureAndStudentId("math", 1L);

		assertTrue(!grades.isEmpty());

		grade.setGrade(new BigDecimal(87.5));
		grade.setLecture("history");

		assertNotNull(studentService.createGrade(grade));

		grades = gradeRepository.findByLectureAndStudentId("history", 1L);

		assertTrue(!grades.isEmpty());

		grade.setGrade(new BigDecimal(90.5));
		grade.setLecture("science");

		assertNotNull(studentService.createGrade(grade));

		grades = gradeRepository.findByLectureAndStudentId("science", 1L);

		assertTrue(!grades.isEmpty());

		assertTrue(gradeRepository.findAll().size() == 4);

	}

	@Test
	@DisplayName("Fail Tests")
	public void failingTests() {
		Grade grade = new Grade();
		grade.setGrade(new BigDecimal(-80.5));
		grade.setStudentId(1L);
		grade.setLecture("math");

		assertThrows(ConstraintViolationException.class, () -> studentService.createGrade(grade));
	}

	@Test
	@DisplayName("Delete Tests")
	public void deleteTests() {
		studentService.deleteGrade(1L);

		assertFalse(gradeRepository.findById(1L).isPresent());

	}

	@Test
	public void studentInformation() {

		GradebookStudent gradebookStudent = studentService.studentInformation(1L);

		assertNotNull(gradebookStudent);
		assertEquals(1L, gradebookStudent.getId());
		assertEquals("Eric", gradebookStudent.getFirstname());
		assertEquals("Bailey", gradebookStudent.getLastname());
		assertEquals("eric@bailey.com", gradebookStudent.getEmail());
		assertTrue(gradebookStudent.getStudentGrades().getGradeResults().size() == 3);
		assertTrue(gradeRepository.findByLectureAndStudentId("math", 1L).size() == 1);
		assertTrue(gradeRepository.findByLectureAndStudentId("science", 1L).size() == 1);
		assertTrue(gradeRepository.findByLectureAndStudentId("history", 1L).size() == 1);
	}

	@Test
	public void studentInformationHttpRequest() throws Exception {

		assertTrue(studentRepository.findById(1L).isPresent());

		MvcResult mvcResult = mockMvc.perform(get("/studentInformation/{id}", 1L)).andExpect(status().isOk())
				.andReturn();

		ModelAndView mav = mvcResult.getModelAndView();

		ModelAndViewAssert.assertViewName(mav, "studentInformation");

	}

	@Test
	public void studentInformationHttpStudentDoesNotExistRequest() throws Exception {

		assertFalse(studentRepository.findById(0L).isPresent());

		MvcResult mvcResult = mockMvc.perform(get("/studentInformation/{id}", 0L)).andExpect(status().isOk())
				.andReturn();

		ModelAndView mav = mvcResult.getModelAndView();

		ModelAndViewAssert.assertViewName(mav, "error");

	}

	@Test
	public void getGradeHttpRequest() throws Exception {
		Grade grade = new Grade();
		grade.setGrade(new BigDecimal(68.50));
		grade.setLecture("math");
		grade.setStudentId(1L);

		List<Grade> grades = new ArrayList<>(Arrays.asList(grade));

		when(studentCreateServiceMock.getGrades()).thenReturn(grades);

		assertIterableEquals(grades, studentCreateServiceMock.getGrades());

		MvcResult mvcResult = mockMvc.perform(get("/grades")).andExpect(status().isOk()).andReturn();

		ModelAndView mav = mvcResult.getModelAndView();

		ModelAndViewAssert.assertViewName(mav, "index");
	}

	@Test
	@DisplayName("Delete Http Tests")
	public void deleteHttpTests() throws Exception {
		assertTrue(gradeRepository.findById(1L).isPresent());

		MvcResult mvcResult = mockMvc.perform(get("/grade/{id}", 1L)).andExpect(status().isOk()).andReturn();

		ModelAndView mav = mvcResult.getModelAndView();

		ModelAndViewAssert.assertViewName(mav, "studentInformation");

		assertFalse(gradeRepository.findById(1L).isPresent());

	}
	
	@Test
	public void gradeByIdHttpRequest() throws Exception {

		assertTrue(gradeRepository.findById(1L).isPresent());

		MvcResult mvcResult = mockMvc.perform(get("/grade/{id}", 1L)).andExpect(status().isOk())
				.andReturn();

		ModelAndView mav = mvcResult.getModelAndView();

		ModelAndViewAssert.assertViewName(mav, "studentInformation");

	}

	@AfterEach
	public void cleanUpDatabase() {
		jdbc.execute(deleteStudentQuery);
		jdbc.execute(deleteGradesQuery);
	}

}
