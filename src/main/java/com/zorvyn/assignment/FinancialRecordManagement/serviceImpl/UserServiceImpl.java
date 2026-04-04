package com.zorvyn.assignment.FinancialRecordManagement.serviceImpl;

import com.zorvyn.assignment.FinancialRecordManagement.constants.Role;
import com.zorvyn.assignment.FinancialRecordManagement.dto.UserRequestDTO;
import com.zorvyn.assignment.FinancialRecordManagement.dto.UserResponseDTO;
import com.zorvyn.assignment.FinancialRecordManagement.dto.UserUpdateRequestDTO;
import com.zorvyn.assignment.FinancialRecordManagement.entities.User;
import com.zorvyn.assignment.FinancialRecordManagement.exception.BadRequestException;
import com.zorvyn.assignment.FinancialRecordManagement.exception.ResourceAlreadyExistException;
import com.zorvyn.assignment.FinancialRecordManagement.exception.ResourceNotFoundException;
import com.zorvyn.assignment.FinancialRecordManagement.mapper.UserMapper;
import com.zorvyn.assignment.FinancialRecordManagement.repository.UserRepository;
import com.zorvyn.assignment.FinancialRecordManagement.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponseDTO createUser(UserRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistException("User with this email already exists");
        }

        if (userRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new ResourceAlreadyExistException("User with this mobile number already exists");
        }

        User user = userMapper.toEntity(request);

        user.setUserId(UUID.randomUUID().toString());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (null == user.getRole()) {
            user.setRole(Role.VIEWER); // default role
        }

        if (null == user.getStatus()) {
            user.setStatus(true);
        }
        if(null == user.getIsDeleted()){
            user.setIsDeleted(false);
        }
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDTO> userResponseDTOS =
                users.stream()
                        .filter(user -> !Boolean.TRUE.equals(user.getIsDeleted()))
                        .map(userMapper::toDTO)
                        .collect(Collectors.toList());
        return userResponseDTOS;
    }

    @Override
    public UserResponseDTO getUserById(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with  user Id: "+userId));

        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new ResourceNotFoundException("User has been removed from the system.");
        }
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(String userId, UserUpdateRequestDTO request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with  user Id: "+userId));


        if (null != request.getMobileNumber() &&
                !request.getMobileNumber().equals(user.getMobileNumber()) &&
                userRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new ResourceAlreadyExistException("Mobile number already exists");
        }

        userMapper.updateEntity(user, request);
        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserStatus(String userId, Boolean status) {
        if (null == status) {
            throw new BadRequestException("Status cannot be null");
        }

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with  user Id: "+userId));
        user.setStatus(status);
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with  user Id: "+userId));
        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new ResourceNotFoundException("User has been removed from the system.");
        }
        user.setIsDeleted(true);
        user.setStatus(false);
        userRepository.save(user);
    }

    @Override
    public List<UserResponseDTO> getAllDeletedUsers() {
        return userRepository.findAllByIsDeletedTrue()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        }
}
