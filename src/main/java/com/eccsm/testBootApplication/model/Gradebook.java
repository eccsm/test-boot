package com.eccsm.testBootApplication.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;

@Component
@Data
@AllArgsConstructor
public class Gradebook {
	private List<GradebookStudent> students = new ArrayList<>();
}
