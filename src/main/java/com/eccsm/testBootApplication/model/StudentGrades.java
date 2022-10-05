package com.eccsm.testBootApplication.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class StudentGrades {
	private List<Grade> gradeResults;

	private List<Grade> mathGradeResults;

	private List<Grade> scienceGradeResults;

	private List<Grade> historyGradeResults;

	public StudentGrades(List<Grade> gradeResults) {
		this.gradeResults = gradeResults;

		mathGradeResults = getFilteredLectureGrades("math");

		scienceGradeResults = getFilteredLectureGrades("science");

		historyGradeResults = getFilteredLectureGrades("history");
	}

	public BigDecimal addGradeResultsForSingleClass(List<Grade> grades, String lecture) {
		BigDecimal result = new BigDecimal(0);

		List<Grade> filteredGrades = grades.stream().filter(g -> lecture.equals(g.getLecture()))
				.collect(Collectors.toList());

		for (Grade grade : filteredGrades)
			result = result.add(grade.getGrade());

		return result;
	}

	public BigDecimal findGradePointAverage(List<Grade> grades, String lecture) {
		int lengthOfGrades = grades.stream().filter(g -> lecture.equals(g.getLecture())).collect(Collectors.toList())
				.size();

		BigDecimal result = new BigDecimal(0);
		if (lengthOfGrades > 0) {
			BigDecimal sum = addGradeResultsForSingleClass(grades, lecture);

			if (sum.compareTo(new BigDecimal(0)) == 1)
				result = sum.divide(BigDecimal.valueOf(lengthOfGrades), 2, RoundingMode.HALF_UP);

		}
		return result;
	}

	private List<Grade> getFilteredLectureGrades(String lecture) {
		if (gradeResults != null)
			return gradeResults.stream().filter(g -> lecture.equals(g.getLecture())).collect(Collectors.toList());

		return new ArrayList<Grade>();
	}

}
