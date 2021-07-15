package com.firstbase.employeemanagement.dto;

import lombok.Data;

/**
 * This Filter object is used to search and filter employee data
 */
@Data
public class EmployeeFilter {
    private String firstName;
    private String lastName;
    private String title;
    private String jobTitle;
}
