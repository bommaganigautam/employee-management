package com.firstbase.employeemanagement.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

/**
 * ORM mapping class for the Employee
 */
@Data
@Entity
@Table(name = "employee")
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @Version
    @Column(columnDefinition = "int default 0")
    protected int version;
    @Id
    @Type(type = "uuid-char")
    @GeneratedValue(generator = "employeeIdGenerator")
    @GenericGenerator(name = "employeeIdGenerator", strategy = "com.firstbase.employeemanagement.idgenerator.EmployeeIdGenerator")
    private UUID id;
    @Column(name = "title")
    private String title;
    @Column(name = "firstname")
    private String firstName;
    @Column(name = "lastname")
    private String lastName;
    @NotNull
    @Column(name = "jobtitle")
    private String jobTitle;
    @Column(name = "pictureurl")
    private String pictureUrl;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;
    @CreationTimestamp
    private Instant updatedAt;

    public Employee(int version, UUID id, String title, String firstName, String lastName, String jobTitle, String pictureUrl, Instant createdAt) {
        this.version = version;
        if (null != id)
            this.id = id;
        else
            this.id = UUID.randomUUID();
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.jobTitle = jobTitle;
        this.pictureUrl = pictureUrl;
        this.createdAt = createdAt;
    }
}
