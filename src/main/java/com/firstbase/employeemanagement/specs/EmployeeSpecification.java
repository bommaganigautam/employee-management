package com.firstbase.employeemanagement.specs;

import com.firstbase.employeemanagement.domain.Employee;
import com.firstbase.employeemanagement.dto.EmployeeFilter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;

/**
 * This is the specification class used to filter the employee results
 */
public class EmployeeSpecification {

    public static Specification<Employee> employeeSearchAndFilter(EmployeeFilter filter) {
        return (root, cq, cb) -> {
            Predicate p = cb.and();
            if (null != filter.getFirstName())
                p.getExpressions().add(cb.equal(root.get("firstName"), filter.getFirstName()));
            if (null != filter.getLastName())
                p.getExpressions().add(cb.equal(root.get("lastName"), filter.getLastName()));
            if (null != filter.getTitle())
                p.getExpressions().add(cb.equal(root.get("title"), filter.getTitle()));
            if (null != filter.getJobTitle())
                p.getExpressions().add(cb.equal(root.get("jobTitle"), filter.getJobTitle()));
            return p;
        };
    }
}
