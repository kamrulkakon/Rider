package com.example.Rider.model.user;

import com.example.Rider.model.AuditModel;
import com.example.Rider.model.enums.ERole;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "role")
public class Role extends AuditModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cam_seq_role")
    @SequenceGenerator(name = "cam_seq_role", sequenceName = "cam_seq_role", initialValue = 1, allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ERole roleName;

    private String value;
}
