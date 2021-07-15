package com.firstbase.employeemanagement.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * This DTO Object is used as
 * employee transfer object
 */
@Data
public class EmployeeDTO {

    private String id;

    @Size(max = 20)
    private String title;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @NotBlank
    @Size(max = 50)
    private String jobTitle;

    @Size(max = 255)
    private String pictureUrl;

    private int version;
}
