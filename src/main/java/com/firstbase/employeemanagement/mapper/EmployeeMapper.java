package com.firstbase.employeemanagement.mapper;

import com.firstbase.employeemanagement.domain.Employee;
import com.firstbase.employeemanagement.dto.EmployeeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

/**
 * This is the mapper method which maps
 * Employee DTO with Employee and vice versa
 */
@Mapper
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    /**
     * This method is used to map UUID in Entity with DTO String
     *
     * @param id
     * @return
     */
    @Named("uuidToString")
    static String uuidToString(UUID id) {
        return id == null ? null : String.valueOf(id);
    }

    /**
     * This method is used to map String in DTO with Entity UUID
     *
     * @param id
     * @return
     */
    @Named("stringToUUID")
    static UUID stringToUUID(String id) {
        return id == null ? null : UUID.fromString(id);
    }

    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToString")
    EmployeeDTO employeeToEmployeeDTO(Employee employee);

    @Mapping(source = "id", target = "id", qualifiedByName = "stringToUUID")
    Employee employeeDTOtoEmployee(EmployeeDTO employeeDTO);

    @Mapping(target = "id", ignore = true)
    void updateEmployeeFromDTO(EmployeeDTO employeeDTO, @MappingTarget Employee employee);
}
