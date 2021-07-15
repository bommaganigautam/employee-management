package com.firstbase.employeemanagement.repository;

import com.firstbase.employeemanagement.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This is the repository interface for the employee
 */
@Repository
public interface EmployeeRepository extends CrudRepository<Employee, UUID> {
    Page<Employee> findAll(Pageable pageable);

    List<Employee> findAll(Specification<Employee> filter);

    Optional<Employee> findById(UUID id);
}
