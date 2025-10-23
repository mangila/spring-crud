package com.github.mangila.app.model.employee.entity;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mangila.app.model.AuditMetadata;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "employee")
@lombok.NoArgsConstructor
public class EmployeeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(
            name = "employee_id",
            nullable = false,
            unique = true,
            length = 36
    )
    private String employeeId;

    @Column(name = "name",
            nullable = false)
    private String name;

    @Column(name = "salary",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal salary;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb",
            nullable = false)
    private ObjectNode attributes;

    @Embedded
    private AuditMetadata auditMetadata;
}
