package com.eccsm.testBootApplication;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.eccsm.testBootApplication.model.Grade;
import com.eccsm.testBootApplication.model.Student;
import com.eccsm.testBootApplication.repository.GradeRepository;
import com.eccsm.testBootApplication.repository.StudentRepository;
import com.eccsm.testBootApplication.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Transactional
public class RestControllerTest {

	private static MockHttpServletRequest request;

	@PersistenceContext
	private EntityManager entityManager;

	@Mock
	StudentService studentServiceMock;

	@Autowired
	private JdbcTemplate jdbc;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private GradeRepository gradeRepository;

	@Autowired
	ObjectMapper objectMapper;

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

	public static final MediaType APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON;

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
	public void getStudentsHttpRequest() throws Exception {

		mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	public void createStudentsHttpRequest() throws Exception {
		Student student = new Student("Mahmut", "Ragıp", "soydan@boydan.com");
		mockMvc.perform(
				post("/student").contentType(APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(student)))
				.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));

		Student created = studentRepository.findByEmail("soydan@boydan.com");
		assertNotNull(created);
	}

	@Test
	public void deleteStudentsHttpRequest() throws Exception {

		mockMvc.perform(delete("/student/{id}", 1L)).andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8)).andExpect(jsonPath("$", hasSize(0)));

		assertFalse(studentRepository.findById(1L).isPresent());
	}

	@Test
	public void deleteStudentsHttpRequestErrorPage() throws Exception {

		assertFalse(studentRepository.findById(0L).isPresent());

		mockMvc.perform(delete("/student/{id}", 0L)).andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.status", is(404)))
				.andExpect(jsonPath("$.message", is("Student or Grade was not found")));

	}

	@Test
	public void studentInformationHttpRequest() throws Exception {
		assertTrue(studentRepository.findById(1L).isPresent());

		mockMvc.perform(get("/studentInformation/{id}", 1L)).andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8)).andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.firstname", is("Eric"))).andExpect(jsonPath("$.lastname", is("Bailey")))
				.andExpect(jsonPath("$.email", is("eric@bailey.com")));
	}

	@Test
	public void studentInformationHttpRequestErrorPage() throws Exception {

		assertFalse(studentRepository.findById(0L).isPresent());

		mockMvc.perform(get("/studentInformation/{id}", 0L)).andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.status", is(404)))
				.andExpect(jsonPath("$.message", is("Student or Grade was not found")));

	}

	@Test
	public void createGradeHttpRequest() throws Exception {
		Grade grade = new Grade();
		grade.setGrade(new BigDecimal(31));
		grade.setLecture("math");
		grade.setStudentId(1L);
		mockMvc.perform(
				post("/grade").contentType(APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(grade)))
				.andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.id", is(1))).andExpect(jsonPath("$.firstname", is("Eric")))
				.andExpect(jsonPath("$.lastname", is("Bailey"))).andExpect(jsonPath("$.email", is("eric@bailey.com")))
				.andExpect(jsonPath("$.studentGrades.mathGradeResults", hasSize(2)));

		Student created = studentRepository.findByEmail("eric@bailey.com");
		assertNotNull(created);
	}

	@Test
	public void createGradeHttpRequestErrorPage() throws Exception {

		assertFalse(studentRepository.findById(0L).isPresent());

		Grade grade = new Grade();
		grade.setGrade(new BigDecimal(31));
		grade.setLecture("math");
		grade.setStudentId(0L);

		mockMvc.perform(
				post("/grade").contentType(APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(grade)))
				.andExpect(status().is4xxClientError()).andExpect(jsonPath("$.status", is(404)))
				.andExpect(jsonPath("$.message", is("Student or Grade was not found")));

	}

	@Test
	public void deleteGradeHttpRequest() throws Exception {
		assertTrue(gradeRepository.findById(1L).isPresent());

		mockMvc.perform(delete("/grade/{id}", 1L)).andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8)).andExpect(jsonPath("$", hasSize(2)));

		assertFalse(gradeRepository.findById(1L).isPresent());
	}

	@Test
	public void deleteGradeHttpRequestrrorPage() throws Exception {
		assertFalse(studentRepository.findById(0L).isPresent());

		mockMvc.perform(delete("/grade/{id}", 0L)).andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.status", is(404)))
				.andExpect(jsonPath("$.message", is("Student or Grade was not found")));

	}

	@AfterEach
	public void cleanUpDatabase() {
		jdbc.execute(deleteStudentQuery);
		jdbc.execute(deleteGradesQuery);
	}

}
