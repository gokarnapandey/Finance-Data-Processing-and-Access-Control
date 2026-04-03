package com.zorvyn.assignment.FinancialRecordManagement.entities;

import com.zorvyn.assignment.FinancialRecordManagement.constants.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String userId; // Business ID (UUID)

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email")
    @Column(unique = true) // Database level constraint
    private String email;

    @NotBlank(message = "Mobile Number is required")
    @Pattern(
            regexp = "^(\\+91|91)?[6-9]\\d{9}$",
            message = "Invalid mobile number"
    )
    @Column(unique = true) // Requirement 5: Data Integrity
    private String mobileNumber;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull(message = "Status is blank")
    private Boolean status;

    @NotNull(message = "Is User Deleted is blank")
    private Boolean isDeleted;
}