package com.example.Rider.model.log;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "error_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nx_seq_auth_error_log")
    @SequenceGenerator(name = "nx_seq_auth_error_log", sequenceName = "nx_seq_auth_error_log", initialValue = 1, allocationSize = 1)
    private Long id;

    private Long tranId;

    private String errorFor;

    @Size(max = 6000)
    private String message;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}
