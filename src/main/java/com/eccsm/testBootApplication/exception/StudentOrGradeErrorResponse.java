package com.eccsm.testBootApplication.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentOrGradeErrorResponse {
	private int status;
	private String message;
	private long timeStamp;

}
