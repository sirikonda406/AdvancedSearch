package com.api.advancedsearch.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "EMPLOYEE")
@NamedEntityGraph(
        name = "Employee.department",
        attributeNodes = @NamedAttributeNode("department")
)
public class Employee implements Serializable {

    @Id
    @Column(name = "EMP_ID")
    private Long empId;

    @Column(name = "EMP_LASTNM")
    private String emplastNm;

    @Column(name = "EMP_FIRSTNM")
    private String  empfirstNm;

    @Column(name = "JOB_NM")
    private String jobNm;

    @Column(name = "MGR_ID", nullable = true)
    private Long managerId;

    @Column(name = "HIREDT")
    private Date hireDt;

    @Column(name = "SALARY")
    private double salary;

    @Column(name = "COMMISSION", nullable = true)
    private double commission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPT_ID")
    @JsonIgnore
    private Department department;

}
