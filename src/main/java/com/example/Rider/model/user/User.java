package com.example.Rider.model.user;

import com.example.Rider.model.AuditModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.util.Collection;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "user")
public class User extends AuditModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cam_seq_user")
    @SequenceGenerator(name = "cam_seq_user", sequenceName = "cam_seq_user", initialValue = 1, allocationSize = 1)
    private Long id;

    @NotEmpty(message = "First name cannot be empty!")
    @Size(min = 3, message = "Size must be getter then 3!")
    private String firstName;

    private String middleName;

    @NotEmpty(message = "Last name cannot be empty!")
    @Size(min = 3, message = "Size must be getter then 3!")
    private String lastName;

    @Transient
    private String displayName;

    @NotBlank
    @Size(min = 5, max = 100, message = "Enter a valid email")
    @Email(message = "Enter a valid email")
    @Column(updatable = false, unique = true)
    private String email;

    @NotBlank
    @Size(max = 120, min = 1, message = "Password must be equal or less than '{max}'")
    @JsonIgnore
    private String password;

    private Boolean isEnabled = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "cam_user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    private String isDeleted = "NO";

    public String getDisplayName() {

        String fullName = "";

        if (this.middleName == null) {
            fullName = this.firstName + ' ' + this.lastName;
        } else {
            fullName = this.firstName + ' ' + this.middleName + ' ' + this.lastName;
        }

        return fullName;
    }
}