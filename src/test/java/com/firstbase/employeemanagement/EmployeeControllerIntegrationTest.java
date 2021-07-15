package com.firstbase.employeemanagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.firstbase.employeemanagement.domain.Employee;
import com.firstbase.employeemanagement.dto.EmployeeDTO;
import com.firstbase.employeemanagement.dto.EmployeeFilter;
import com.firstbase.employeemanagement.repository.EmployeeRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@ActiveProfiles("test")
public class EmployeeControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Deleting all records after all tests have been performed
     */
    @After
    public void resetDb() {
        employeeRepository.deleteAll();
    }

    /**
     * This method inserts the test data before the test cases,
     * so we can test the data
     */
    @Before
    public void insertTestData() {
        Employee EMPLOYEE_1 = new Employee(0, UUID.fromString("9e54ad04-f58a-482b-ad49-c2581c096d7c"), "Miss", "Kathrine", "Dale",
                "Backend Developer", "https://randomuser.me/api/portraits/women/67.jpg", Instant.now(), null);

        Employee EMPLOYEE_2 = new Employee(0, UUID.fromString("cb0a0470-7b1c-440d-b9b5-2f67300f4350"), "Mr", "John", "Wick",
                "Front End Developer", "https://randomuser.me/api/portraits/men/65.jpg", Instant.now(), null);

        Employee EMPLOYEE_3 = new Employee(0, UUID.fromString("fb0a0470-7b1c-440d-c9b5-2f67300f4350"), "Mr", "Carlos", "Mike",
                "Front End Developer", "https://randomuser.me/api/portraits/men/65.jpg", Instant.now(), null);

        List<Employee> employeeList = Arrays.asList(EMPLOYEE_1, EMPLOYEE_2, EMPLOYEE_3);
        employeeRepository.saveAll(employeeList);
    }

    @Test
    public void insertSampleData_withoutAnyParams() throws Exception {
        mockMvc.perform(post("/employees/v1/insertSomeSampleData")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)));
    }

    @Test
    public void insertSampleData_withParams() throws Exception {
        mockMvc.perform(post("/employees/v1/insertSomeSampleData/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void getAllEmployees_success() throws Exception {
        mockMvc.perform(get("/employees/v1/getEmployees")
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void getEmployeeById_success() throws Exception {
        mockMvc.perform(get("/employees/v1/getEmployeeById/9e54ad04-f58a-482b-ad49-c2581c096d7c")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName", is("Dale")));
    }

    @Test
    public void getEmployeeById_notfound() throws Exception {
        mockMvc.perform(get("/employees/v1/getEmployeeById/9e54ad04-f58a-482b-ad49-c2581c09677c")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void searchEmployeeWithFirstName() throws Exception {

        String requestJson = prepareJsonStringForEmployeeFilter("Kathrine", null, null, null);

        mockMvc.perform(post("/employees/v1/searchAndFilterEmployees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName", is("Dale")));
    }

    @Test
    public void searchEmployeeWithLastName() throws Exception {

        String requestJson = prepareJsonStringForEmployeeFilter(null, "Mike", null, null);

        mockMvc.perform(post("/employees/v1/searchAndFilterEmployees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName", is("Mike")));
    }


    @Test
    public void searchEmployeeWithNullFilter() throws Exception {
        String requestJson = prepareJsonStringForEmployeeFilter(null, null, null, null);

        mockMvc.perform(post("/employees/v1/searchAndFilterEmployees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }


    @Test
    public void createEmployee_success() throws Exception {
        String requestJson =
                prepareJsonStringForEmployeeDTO("Mr", "Mark", "John", "Software Engineer",
                        "https://pictureUrl797.jpg", 0);

        mockMvc.perform(post("/employees/v1/createEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());
    }


    @Test
    public void createEmployee_NoJobTitle() throws Exception {
        String requestJson =
                prepareJsonStringForEmployeeDTO("Mr", "Mark", "John", null,
                        "https://pictureUrl797.jpg", 0);

        mockMvc.perform(post("/employees/v1/createEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void updateEmployee_Success() throws Exception {
        String requestJson =
                prepareJsonStringForEmployeeDTO("Mr", "John", "Tilbery", "Product Manager",
                        null, 0);

        mockMvc.perform(put("/employees/v1/updateEmployee/cb0a0470-7b1c-440d-b9b5-2f67300f4350")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());

    }


    @Test
    public void updateEmployee_VersionError() throws Exception {
        String requestJson =
                prepareJsonStringForEmployeeDTO("Miss", "Kathrine", "Dale", "Software Manager",
                        null, 0);

        mockMvc.perform(put("/employees/v1/updateEmployee/9e54ad04-f58a-482b-ad49-c2581c096d7c")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        //updating a record without incrementing the version
        String requestJson2 =
                prepareJsonStringForEmployeeDTO("Miss", "Kathrine", "Dale", "HR Manager",
                        null, 0);


        mockMvc.perform(put("/employees/v1/updateEmployee/9e54ad04-f58a-482b-ad49-c2581c096d7c")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson2))
                .andDo(print())
                .andExpect(status().is5xxServerError());

    }


    @Test
    public void updateEmployee_With_NullJobTitle_Exception() throws Exception {
        String requestJson =
                prepareJsonStringForEmployeeDTO("Mr", "John", "Tilbery", null,
                        null, 0);

        mockMvc.perform(put("/employees/v1/updateEmployee/cb0a0470-7b1c-440d-b9b5-2f67300f4350")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().is5xxServerError());

    }


    /**
     * This is a util method used to create
     * a string value to be used in post request
     *
     * @param firstName
     * @param lastName
     * @param title
     * @param jobTitle
     * @return
     */
    public String prepareJsonStringForEmployeeFilter(String firstName, String lastName, String title, String jobTitle)
            throws JsonProcessingException {
        EmployeeFilter employeeFilter = new EmployeeFilter();
        if (null != firstName)
            employeeFilter.setFirstName(firstName);
        if (null != lastName)
            employeeFilter.setLastName(lastName);
        if (null != title)
            employeeFilter.setTitle(title);
        if (null != jobTitle)
            employeeFilter.setJobTitle(jobTitle);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(employeeFilter);
    }

    /**
     * This method is used to create a string
     * value of employee DTO
     *
     * @param title
     * @param firstName
     * @param lastName
     * @param jobTitle
     * @param pictureUrl
     * @return
     * @throws JsonProcessingException
     */
    public String prepareJsonStringForEmployeeDTO(String title, String firstName, String lastName, String jobTitle,
                                                  String pictureUrl, int version) throws JsonProcessingException {

        EmployeeDTO employeeDTO = new EmployeeDTO();
        if (null != firstName)
            employeeDTO.setFirstName(firstName);
        if (null != lastName)
            employeeDTO.setLastName(lastName);
        if (null != title)
            employeeDTO.setTitle(title);
        if (null != jobTitle)
            employeeDTO.setJobTitle(jobTitle);
        if (null != pictureUrl)
            employeeDTO.setPictureUrl(pictureUrl);
        if (version > 0)
            employeeDTO.setVersion(version);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(employeeDTO);
    }


}
