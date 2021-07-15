package com.firstbase.employeemanagement.service;

import com.firstbase.employeemanagement.dto.EmployeeDTO;
import com.firstbase.employeemanagement.dto.EmployeeFilter;
import com.firstbase.employeemanagement.exception.JSONParseException;
import com.firstbase.employeemanagement.exception.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {

    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);

    EmployeeDTO updateEmployee(UUID employeeId, EmployeeDTO employeeDTO);

    EmployeeDTO getEmployeeById(UUID employeeId) throws ResourceNotFoundException;

    List<EmployeeDTO> searchAndFilterEmployees(EmployeeFilter filter);

    List<EmployeeDTO> getAllEmployees(int page, int pageSize, String sortBy);

    List<EmployeeDTO> insertSampleData(int noOfSampleRecords) throws JSONParseException;

}
