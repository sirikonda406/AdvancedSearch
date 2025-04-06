package com.api.advancedsearch.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "DEPARTMENT")
public class Department implements Serializable {

    @Id
    private Long deptId;

    @Column(name = "DEPT_NAME")
    private String deptName;

    @OneToMany(mappedBy = "department")
    @JsonIgnore
    private List<Employee> emps;

}
