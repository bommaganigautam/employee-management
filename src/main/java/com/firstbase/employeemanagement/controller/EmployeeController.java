package com.firstbase.employeemanagement.controller;

import com.firstbase.employeemanagement.dto.EmployeeDTO;
import com.firstbase.employeemanagement.dto.EmployeeFilter;
import com.firstbase.employeemanagement.service.EmployeeService;
import io.swagger.annotations.ApiOperation;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This is the controller class for the Employee
 */
@RestController
@RequestMapping("employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping(value = {"/v1/insertSomeSampleData/{noOfSampleRecords}", "/v1/insertSomeSampleData"})
    @ApiOperation(value = "Inserts some sample data into the database",
            notes = "Provide a number of sample data to be inserted, " +
                    "if not provided 10 sample records will be inserted",
            response = EmployeeDTO.class)
    public ResponseEntity<List<EmployeeDTO>> insertSampleData(@PathVariable Optional<Integer> noOfSampleRecords) throws JSONException {
        logger.debug("In EmployeeController of method: insertSampleData");
        return ResponseEntity.ok().body(employeeService.insertSampleData(noOfSampleRecords.orElse(10)));
    }

    @GetMapping("/v1/getEmployees")
    @ApiOperation(value = "Finds all employees",
            notes = "Provide a pageNumber, pageSize and sort By to fetch results, if not provided pageNumber of 0, " +
                    "pageSize of 5 and results sorted by lastName are fetched",
            response = EmployeeDTO.class)
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(@RequestParam Optional<Integer> pageNumber,
                                                             @RequestParam Optional<Integer> pageSize,
                                                             @RequestParam Optional<String> sortBy) {
        logger.debug("In EmployeeController of method: getAllEmployees");
        return ResponseEntity.ok().body(employeeService.getAllEmployees(pageNumber.orElse(0),
                pageSize.orElse(5), sortBy.orElse("lastName")));
    }

    @ApiOperation(value = "Finds employee by id",
            notes = "Provide a employee id, if found will return the employee else no found message will be returned",
            response = EmployeeDTO.class)
    @GetMapping("/v1/getEmployeeById/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable UUID id) {
        logger.debug("In EmployeeController of method: getEmployeeById with id: ", id);
        return ResponseEntity.ok().body(employeeService.getEmployeeById(id));
    }

    @ApiOperation(value = "Searches and filters all employees",
            notes = "Provide a value in the body such as employee id, " +
                    "last name, e.t.c and it will return the employees that match",
            response = EmployeeDTO.class)
    @PostMapping("/v1/searchAndFilterEmployees")
    public ResponseEntity<List<EmployeeDTO>> searchAndFilterEmployees(@RequestBody EmployeeFilter filter) {
        logger.debug("In EmployeeController of method: searchAndFilterEmployees with filter: " + filter);
        return ResponseEntity.ok().body(employeeService.searchAndFilterEmployees(filter));
    }

    @ApiOperation(value = "Creates a new employee in the database",
            notes = "Provide employee values and the employee will be created",
            response = EmployeeDTO.class)
    @PostMapping("/v1/createEmployee")
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        logger.debug("In EmployeeController of method: createEmployee with employeeDTO: " + employeeDTO);
        return ResponseEntity.ok().body(employeeService.createEmployee(employeeDTO));
    }

    @ApiOperation(value = "Updates a employee info in the database",
            notes = "Provide employee values to be updated",
            response = EmployeeDTO.class)
    @PutMapping("/v1/updateEmployee/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable UUID id, @RequestBody EmployeeDTO employeeDTO) {
        logger.debug("In EmployeeController of method: updateEmployee with employeeDTO: " + employeeDTO);
        return ResponseEntity.ok().body(employeeService.updateEmployee(id, employeeDTO));
    }
}
