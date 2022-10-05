package com.eccsm.testBootApplication.exception;

public class StudentOrGradeNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public StudentOrGradeNotFoundException(String message) {
		super(message);
	}

	public StudentOrGradeNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public StudentOrGradeNotFoundException(Throwable cause) {
		super(cause);
	}
}
