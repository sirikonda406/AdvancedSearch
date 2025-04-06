package com.api.advancedsearch.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SALARY_GRADE")
public class SalaryGrade {

    @Id
    private Long grade;
    private double minSalary;
    private double maxSalary;
}
