package com.eccsm.testBootApplication.model;

import java.math.BigDecimal;

import javax.persistence.*;
import javax.validation.constraints.*;

import lombok.Data;

@Entity
@Table(name = "grades")
@Data
public class Grade {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private Long studentId;

	@Column
	private String lecture;

	@Column
	@DecimalMin(value="0.00")
	@DecimalMax(value="100.00")
	private BigDecimal grade;
}
