package com.hospitalManagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Indexed;

import java.time.LocalDateTime;

@Entity
@ToString
@Getter
@Setter
@Table(
        name = "Patient",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_patient_email", columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_patient_email", columnList = "email")
        }
)
public class Patient {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    @ToString.Exclude
    private LocalDateTime birthDate;

//    @Column(unique = true)
    private String email;

    private String gender;

    private String bloodGroup;
}
