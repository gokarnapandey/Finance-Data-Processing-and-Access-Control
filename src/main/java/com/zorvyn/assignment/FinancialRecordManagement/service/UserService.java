package com.zorvyn.assignment.FinancialRecordManagement.service;


import com.zorvyn.assignment.FinancialRecordManagement.dto.UserRequestDTO;
import com.zorvyn.assignment.FinancialRecordManagement.dto.UserResponseDTO;
import com.zorvyn.assignment.FinancialRecordManagement.dto.UserUpdateRequestDTO;

import java.util.List;


public interface UserService {

    UserResponseDTO createUser(UserRequestDTO request);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(String userId);

    UserResponseDTO updateUser(String userId, UserUpdateRequestDTO request);

    UserResponseDTO updateUserStatus(String userId, Boolean status);

    void deleteUser(String userId);

    List<UserResponseDTO> getAllDeletedUsers();
}
