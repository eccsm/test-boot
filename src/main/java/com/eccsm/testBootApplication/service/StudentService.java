package com.eccsm.testBootApplication.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.eccsm.testBootApplication.model.Grade;
import com.eccsm.testBootApplication.model.GradebookStudent;
import com.eccsm.testBootApplication.model.Student;
import com.eccsm.testBootApplication.model.StudentGrades;
import com.eccsm.testBootApplication.repository.GradeRepository;
import com.eccsm.testBootApplication.repository.StudentRepository;

@Service
@Transactional
public class StudentService {

	@Autowired
	StudentRepository studentRepo;

	@Autowired
	GradeRepository gradeRepo;

	public Student createStudent(Student student) {
		student.setId(0L);
		return studentRepo.save(student);
	}

	public boolean isStudentNull(Long id) {
		Optional<Student> student = studentRepo.findById(id);

		if (student.isPresent())
			return false;

		return true;
	}

	public boolean isGradeNull(Long id) {
		Optional<Grade> grade = gradeRepo.findById(id);

		if (grade.isPresent())
			return false;

		return true;
	}

	public void deleteStudent(long id) {
		if (!isStudentNull(id))
			studentRepo.deleteById(id);
	}

	public List<Student> getStudents() {
		return studentRepo.findAll();
	}

	public List<Grade> getGrades() {
		return gradeRepo.findAll();
	}

	public Grade createGrade(Grade grade) {
		if (!isStudentNull(grade.getStudentId()))
			return gradeRepo.save(grade);

		return null;
	}

	public void deleteGrade(Long id) {
		if (!isGradeNull(id))
			gradeRepo.deleteById(id);
	}

	public GradebookStudent studentInformation(Long id) {

		if (isStudentNull(id))
			return null;

		Optional<Student> student = studentRepo.findById(id);

		List<Grade> grades = gradeRepo.findByStudentId(id);

		StudentGrades studentGrades = new StudentGrades(grades);

		GradebookStudent gradebookStudent = new GradebookStudent(student.get().getId(), studentGrades,
				student.get().getFirstname(), student.get().getLastname(), student.get().getEmail());

		return gradebookStudent;
	}

	public void configureStudentInformationModel(Long id, Model m) {

		GradebookStudent gradebookStudent = studentInformation(id);

		m.addAttribute("student", gradebookStudent);

		StudentGrades studentGrades = gradebookStudent.getStudentGrades();
		BigDecimal mathAverage = studentGrades.findGradePointAverage(studentGrades.getGradeResults(), "math");
		BigDecimal scienceAverage = studentGrades.findGradePointAverage(studentGrades.getGradeResults(), "science");
		BigDecimal historyAverage = studentGrades.findGradePointAverage(studentGrades.getGradeResults(), "history");
		if (mathAverage.compareTo(new BigDecimal(0)) > 0)
			m.addAttribute("mathAverage", mathAverage);
		if (scienceAverage.compareTo(new BigDecimal(0)) > 0)
			m.addAttribute("scienceAverage", scienceAverage);
		if (historyAverage.compareTo(new BigDecimal(0)) > 0)
			m.addAttribute("historyAverage", historyAverage);

	}

	public Grade getGradeById(Long id) {

		if (isGradeNull(id))
			return null;

		Optional<Grade> grade = gradeRepo.findById(id);

		return grade.get();
	}
}
