package com.firstbase.employeemanagement.service.impl;

import com.firstbase.employeemanagement.domain.Employee;
import com.firstbase.employeemanagement.dto.EmployeeDTO;
import com.firstbase.employeemanagement.dto.EmployeeFilter;
import com.firstbase.employeemanagement.exception.JSONParseException;
import com.firstbase.employeemanagement.exception.ResourceNotFoundException;
import com.firstbase.employeemanagement.handler.RestTemplateResponseErrorHandler;
import com.firstbase.employeemanagement.mapper.EmployeeMapper;
import com.firstbase.employeemanagement.repository.EmployeeRepository;
import com.firstbase.employeemanagement.service.EmployeeService;
import com.firstbase.employeemanagement.specs.EmployeeSpecification;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.persistence.OptimisticLockException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This is the employee service class
 */
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RestTemplate restTemplate;
    Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, RestTemplateBuilder restTemplateBuilder) {
        this.employeeRepository = employeeRepository;
        this.restTemplate = restTemplateBuilder
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
    }

    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Employee employee = EmployeeMapper.INSTANCE.employeeDTOtoEmployee(employeeDTO);
        return EmployeeMapper.INSTANCE.employeeToEmployeeDTO(employeeRepository.save(employee));
    }

    @Override
    public EmployeeDTO updateEmployee(UUID employeeId, EmployeeDTO employeeDTO) {
        Optional<Employee> employeeFromDb = employeeRepository.findById(employeeId);
        if (employeeFromDb.isPresent()) {
            Employee employeeToBeUpdated = employeeFromDb.get();
            if (employeeToBeUpdated.getVersion() != employeeDTO.getVersion())
                throw new OptimisticLockException("You are trying to update an old record of employee, " +
                        "please reload the employee record and update again");
            else {
                EmployeeMapper.INSTANCE.updateEmployeeFromDTO(employeeDTO, employeeToBeUpdated);
                return EmployeeMapper.INSTANCE.employeeToEmployeeDTO(employeeRepository.save(employeeToBeUpdated));
            }
        } else
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
    }

    @Override
    public EmployeeDTO getEmployeeById(UUID employeeId) throws ResourceNotFoundException {
        Optional<Employee> employeeFromDb = employeeRepository.findById(employeeId);
        if (employeeFromDb.isPresent())
            return EmployeeMapper.INSTANCE.employeeToEmployeeDTO(employeeFromDb.get());
        else
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
    }

    @Override
    public List<EmployeeDTO> searchAndFilterEmployees(EmployeeFilter filter) {
        if (null != filter) {
            List<EmployeeDTO> employeeDTOList = new ArrayList<>();
            List<Employee> employeeListFromDb = employeeRepository.findAll(
                    EmployeeSpecification.employeeSearchAndFilter(filter));

            if (null != employeeListFromDb) {
                for (Employee employee : employeeListFromDb) {
                    employeeDTOList.add(EmployeeMapper.INSTANCE.employeeToEmployeeDTO(employee));
                }
            }
            return employeeDTOList;
        } else
            return null;
    }

    @Override
    public List<EmployeeDTO> getAllEmployees(int page, int pageSize, String sortBy) {
        List<Employee> employeeListFromDb = employeeRepository.findAll(
                PageRequest.of(
                        page,
                        pageSize,
                        Sort.Direction.ASC, sortBy
                )
        ).toList();
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();


        for (Employee employee : employeeListFromDb) {
            employeeDTOList.add(EmployeeMapper.INSTANCE.employeeToEmployeeDTO(employee));
        }

        return employeeDTOList;
    }

    @Override
    public List<EmployeeDTO> insertSampleData(int noOfSampleRecords) throws JSONParseException {

        List<Employee> employeeList = new ArrayList<>();
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();

        for (int i = 0; i < noOfSampleRecords; i++) {
            employeeList.add(fetchEmplDtlsFrmUsrRndmApi());
        }

        employeeList = (List<Employee>) employeeRepository.saveAll(employeeList);

        for (Employee employee : employeeList) {
            employeeDTOList.add(EmployeeMapper.INSTANCE.employeeToEmployeeDTO(employee));
        }

        return employeeDTOList;
    }

    /**
     * This method is used to read the random user
     * Api and  map the response to the object
     *
     * @return
     * @throws JSONException
     */
    public Employee fetchEmplDtlsFrmUsrRndmApi() throws JSONParseException {

        logger.debug("In EmployeeServiceImpl in method: fetchEmplDtlsFrmUsrRndmApi");

        Employee employee = new Employee();
        try {
            String responseBody
                    = restTemplate.getForEntity("https://randomuser.me/api/", String.class).getBody();

            logger.debug("response body: " + responseBody);

            if (null != responseBody) {
                JSONObject obj = new JSONObject(responseBody);
                JSONArray userArray = obj.getJSONArray("results");

                for (int i = 0; i < userArray.length(); i++) {
                    JSONObject userObject = userArray.getJSONObject(i);

                    //checking if the response object has name attributes
                    if (userObject.has("name")) {
                        JSONObject nameObject = userObject.getJSONObject("name");
                        if (nameObject.has("first"))
                            employee.setFirstName(nameObject.getString("first"));
                        if (nameObject.has("last"))
                            employee.setLastName(nameObject.getString("last"));
                        if (nameObject.has("title"))
                            employee.setTitle(nameObject.getString("title"));
                    }

                    //checking if the response object has picture object and setting the values
                    if (userObject.has("picture")) {
                        JSONObject pictureObject = userObject.getJSONObject("picture");
                        if (pictureObject.has("large"))
                            employee.setPictureUrl(pictureObject.getString("large"));
                    }

                    //since the job title is not returned from the API, setting a static value
                    employee.setJobTitle("Software Developer");
                }
            }
        } catch (JSONException e) {
            throw new JSONParseException("Exception in parsing the JSON response from Random API", e.getCause());
        }

        logger.debug("end of method: fetchEmplDtlsFrmUsrRndmApi in EmployeeServiceImpl");
        return employee;
    }
}
