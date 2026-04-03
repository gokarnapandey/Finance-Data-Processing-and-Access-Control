package com.zorvyn.assignment.FinancialRecordManagement.mapper;


import com.zorvyn.assignment.FinancialRecordManagement.dto.UserRequestDTO;
import com.zorvyn.assignment.FinancialRecordManagement.dto.UserResponseDTO;
import com.zorvyn.assignment.FinancialRecordManagement.dto.UserUpdateRequestDTO;
import com.zorvyn.assignment.FinancialRecordManagement.entities.User;
import com.zorvyn.assignment.FinancialRecordManagement.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component // Inject PasswordEncoder if needed here, or handle in Service
public class UserMapper {

    /**
     * Converts a New User Request to a Database Entity.
     * Note: Password encoding should ideally happen in the Service layer,
     * but we ensure all DTO fields are captured here.
     */
    public User toEntity(UserRequestDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setMobileNumber(dto.getMobileNumber());
        user.setPassword(dto.getPassword()); // Will be encoded in Service
        user.setRole(dto.getRole());
        user.setStatus(dto.getStatus());
        user.setIsDeleted(dto.getIsDeleted()); // Added from your latest DTO

        return user;
    }

    public UserResponseDTO toDTO(User user) {
        if (user == null) return null;

        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setMobileNumber(user.getMobileNumber());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setIsDeleted(user.getIsDeleted());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        return dto;
    }

    /**
     * Updates an existing user based on Admin input.
     * Email is excluded as per your business logic.
     */
    public void updateEntity(User user, UserUpdateRequestDTO dto) {
        if (dto == null || user == null) {
            throw new BadRequestException("Update request data or target user is missing.");
        }

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getMobileNumber() != null) user.setMobileNumber(dto.getMobileNumber());

        if (dto.getPassword() != null) user.setPassword(dto.getPassword());

        if (dto.getStatus() != null) user.setStatus(dto.getStatus());
        if (dto.getRole() != null) user.setRole(dto.getRole());
    }
}
