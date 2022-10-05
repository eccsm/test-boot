package com.eccsm.testBootApplication.model;

public class GradebookStudent extends Student {
	public GradebookStudent(Long id, StudentGrades studentGrades, String firstname, String lastname, String email) {
		super(firstname, lastname, email);
		this.studentGrades = studentGrades;
		this.id = id;
	}

	private Long id;

	private StudentGrades studentGrades;

	public StudentGrades getStudentGrades() {
		return studentGrades;
	}

	public void setStudentGrades(StudentGrades studentGrades) {
		this.studentGrades = studentGrades;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

}
