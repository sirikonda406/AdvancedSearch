package com.api.advancedsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDto {

    private Long deptId;
    private String deptName;
}
