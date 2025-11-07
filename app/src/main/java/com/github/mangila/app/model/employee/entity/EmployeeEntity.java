package com.github.mangila.app.model.employee.entity;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.model.AuditMetadata;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

/**
 * Entity is a POJO representation of a database table.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "employees")
@lombok.NoArgsConstructor
@lombok.Data
public class EmployeeEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "first_name",
            nullable = false)
    private String firstName;

    @Column(name = "last_name",
            nullable = false)
    private String lastName;

    @Column(name = "salary",
            nullable = false,
            precision = 10,
            scale = 2)
    private BigDecimal salary;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb",
            nullable = false)
    private ObjectNode attributes;

    @Embedded
    private AuditMetadata auditMetadata;
}