package com.zorvyn.assignment.FinancialRecordManagement.serviceImplTest;

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
import com.zorvyn.assignment.FinancialRecordManagement.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequestDTO requestDTO;
    private User mockUser;

    @BeforeEach
    void setUp() {
        requestDTO = new UserRequestDTO();
        requestDTO.setEmail("test@example.com");
        requestDTO.setMobileNumber("1234567890");
        requestDTO.setPassword("rawPassword");

        mockUser = new User();
        mockUser.setEmail("test@example.com");
    }

    @Test
    void createUser_Success() {
        // 1. Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByMobileNumber(anyString())).thenReturn(false);
        when(userMapper.toEntity(any(UserRequestDTO.class))).thenReturn(mockUser);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setUserId("uuid-123");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDTO expectedResponse = new UserResponseDTO();
        when(userMapper.toDTO(any(User.class))).thenReturn(expectedResponse);

        // 2. Act
        UserResponseDTO result = userService.createUser(requestDTO);

        // 3. Assert
        assertNotNull(result);
        assertEquals(Role.VIEWER, mockUser.getRole()); // Verify default role logic
        assertTrue(mockUser.getStatus());              // Verify default status
        assertFalse(mockUser.getIsDeleted());          // Verify isDeleted logic
        assertEquals("encodedPassword", mockUser.getPassword());

        verify(userRepository).save(mockUser);
    }

    @Test
    void createUser_ThrowsException_WhenEmailExists() {
        // Arrange
        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(ResourceAlreadyExistException.class, () -> {
            userService.createUser(requestDTO);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_ThrowsException_WhenMobileExists() {
        // Arrange
        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByMobileNumber(requestDTO.getMobileNumber())).thenReturn(true);

        // Act & Assert
        assertThrows(ResourceAlreadyExistException.class, () -> {
            userService.createUser(requestDTO);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void getAllUsers_ShouldReturnOnlyActiveUsers() {
        // 1. Arrange
        User activeUser = new User();
        activeUser.setUserId("1");
        activeUser.setIsDeleted(false);

        User deletedUser = new User();
        deletedUser.setUserId("2");
        deletedUser.setIsDeleted(true);

        List<User> mockUserList = List.of(activeUser, deletedUser);

        // Stub the repository to return both users
        when(userRepository.findAll()).thenReturn(mockUserList);

        // Stub the mapper for the active user
        UserResponseDTO activeUserDTO = new UserResponseDTO();
        activeUserDTO.setUserId("1");
        when(userMapper.toDTO(activeUser)).thenReturn(activeUserDTO);

        // 2. Act
        List<UserResponseDTO> result = userService.getAllUsers();

        // 3. Assert
        assertNotNull(result);
        assertEquals(1, result.size(), "Result should only contain the non-deleted user");
        assertEquals("1", result.get(0).getUserId());

        // Verify that the mapper was NEVER called for the deleted user
        verify(userMapper, times(1)).toDTO(activeUser);
        verify(userMapper, never()).toDTO(deletedUser);
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsersExist() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of());

        // Act
        List<UserResponseDTO> result = userService.getAllUsers();

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
        verifyNoInteractions(userMapper);
    }


    @Test
    void getUserById_Success() {
        // 1. Arrange
        String userId = "user-123";
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setIsDeleted(false);

        UserResponseDTO expectedDto = new UserResponseDTO();
        expectedDto.setUserId(userId);

        // Stubbing repository to find the user
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(mockUser));
        // Stubbing mapper to return the DTO
        when(userMapper.toDTO(mockUser)).thenReturn(expectedDto);

        // 2. Act
        UserResponseDTO result = userService.getUserById(userId);

        // 3. Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(userRepository).findByUserId(userId);
        verify(userMapper).toDTO(mockUser);
    }

    @Test
    void getUserById_ThrowsException_WhenUserNotFound() {
        // 1. Arrange
        String userId = "non-existent-id";
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // 2. Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(userId);
        });

        assertTrue(exception.getMessage().contains("User not found"));
        verify(userMapper, never()).toDTO(any());
    }

    @Test
    void getUserById_ThrowsException_WhenUserIsDeleted() {
        // 1. Arrange
        String userId = "deleted-user-123";
        User deletedUser = new User();
        deletedUser.setUserId(userId);
        deletedUser.setIsDeleted(true); // User is soft-deleted

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(deletedUser));

        // 2. Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(userId);
        });

        assertEquals("User has been removed from the system.", exception.getMessage());
        verify(userMapper, never()).toDTO(any());
    }

    @Test
    void updateUser_Success() {
        // 1. Arrange
        String userId = "user-123";
        UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO();
        updateRequest.setMobileNumber("9876543210");
        updateRequest.setName("New Name");

        User existingUser = new User();
        existingUser.setUserId(userId);
        existingUser.setMobileNumber("1112223333");

        // Stub: User found in DB
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(existingUser));

        // Stub: New mobile number is not taken by someone else
        when(userRepository.existsByMobileNumber(updateRequest.getMobileNumber())).thenReturn(false);

        // Stub: Repository save
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Stub: Mapper to DTO
        UserResponseDTO expectedResponse = new UserResponseDTO();
        expectedResponse.setUserId(userId);
        when(userMapper.toDTO(existingUser)).thenReturn(expectedResponse);

        // 2. Act
        UserResponseDTO result = userService.updateUser(userId, updateRequest);

        // 3. Assert
        assertNotNull(result);
        verify(userRepository).findByUserId(userId);
        verify(userMapper).updateEntity(existingUser, updateRequest);
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_ThrowsException_WhenMobileNumberAlreadyExists() {
        // 1. Arrange
        String userId = "user-123";
        UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO();
        updateRequest.setMobileNumber("9999999999"); // New number

        User existingUser = new User();
        existingUser.setUserId(userId);
        existingUser.setMobileNumber("1112223333"); // Old number

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(existingUser));

        // Simulate that another user already has the number 9999999999
        when(userRepository.existsByMobileNumber(updateRequest.getMobileNumber())).thenReturn(true);

        // 2. Act & Assert
        assertThrows(ResourceAlreadyExistException.class, () -> {
            userService.updateUser(userId, updateRequest);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_Success_WhenMobileNumberIsSameAsCurrent() {
        // 1. Arrange
        String userId = "user-123";
        String sameMobile = "1112223333";

        UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO();
        updateRequest.setMobileNumber(sameMobile); // Keeping the same number

        User existingUser = new User();
        existingUser.setUserId(userId);
        existingUser.setMobileNumber(sameMobile);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(userMapper.toDTO(any())).thenReturn(new UserResponseDTO());

        // 2. Act
        UserResponseDTO result = userService.updateUser(userId, updateRequest);

        // 3. Assert
        assertNotNull(result);
        // Crucial: check that existsByMobileNumber was NEVER called because numbers match
        verify(userRepository, never()).existsByMobileNumber(anyString());
    }

    @Test
    void updateUserStatus_Success() {
        // 1. Arrange
        String userId = "user-123";
        Boolean newStatus = false; // Deactivating user

        User existingUser = new User();
        existingUser.setUserId(userId);
        existingUser.setStatus(true); // Current status is active

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserResponseDTO expectedResponse = new UserResponseDTO();
        expectedResponse.setStatus(newStatus);
        when(userMapper.toDTO(existingUser)).thenReturn(expectedResponse);

        // 2. Act
        UserResponseDTO result = userService.updateUserStatus(userId, newStatus);

        // 3. Assert
        assertNotNull(result);
        assertFalse(existingUser.getStatus(), "The user entity status should have been updated");
        verify(userRepository).save(existingUser);
        verify(userMapper).toDTO(existingUser);
    }

    @Test
    void updateUserStatus_ThrowsException_WhenStatusIsNull() {
        // 1. Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.updateUserStatus("user-123", null);
        });

        assertEquals("Status cannot be null", exception.getMessage());
        verifyNoInteractions(userRepository); // Optimization: ensure DB isn't hit if validation fails
    }

    @Test
    void updateUserStatus_ThrowsException_WhenUserNotFound() {
        // 1. Arrange
        String userId = "unknown-id";
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // 2. Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUserStatus(userId, true);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_Success() {
        // 1. Arrange
        String userId = "user-123";
        User existingUser = new User();
        existingUser.setUserId(userId);
        existingUser.setIsDeleted(false);
        existingUser.setStatus(true);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(existingUser));

        // 2. Act
        userService.deleteUser(userId);

        // 3. Assert
        assertTrue(existingUser.getIsDeleted(), "isDeleted should be set to true");
        assertFalse(existingUser.getStatus(), "Status should be set to false upon deletion");

        // Verify the updated entity was actually saved to the database
        verify(userRepository).save(existingUser);
    }

    @Test
    void deleteUser_ThrowsException_WhenUserNotFound() {
        // 1. Arrange
        String userId = "missing-id";
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // 2. Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_ThrowsException_WhenUserAlreadyDeleted() {
        // 1. Arrange
        String userId = "already-deleted-id";
        User deletedUser = new User();
        deletedUser.setUserId(userId);
        deletedUser.setIsDeleted(true); // Already deleted

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(deletedUser));

        // 2. Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });

        assertEquals("User has been removed from the system.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }


    @Test
    void getAllDeletedUsers_ShouldReturnOnlyDeletedUsers() {
        // 1. Arrange
        User deletedUser1 = new User();
        deletedUser1.setUserId("user-101");
        deletedUser1.setIsDeleted(true);

        User deletedUser2 = new User();
        deletedUser2.setUserId("user-102");
        deletedUser2.setIsDeleted(true);

        List<User> mockDeletedList = List.of(deletedUser1, deletedUser2);

        // Stub the repository method specific to deleted users
        when(userRepository.findAllByIsDeletedTrue()).thenReturn(mockDeletedList);

        // Stub the mapper for both users
        when(userMapper.toDTO(any(User.class))).thenReturn(new UserResponseDTO());

        // 2. Act
        List<UserResponseDTO> result = userService.getAllDeletedUsers();

        // 3. Assert
        assertNotNull(result);
        assertEquals(2, result.size(), "Should return exactly 2 deleted users");

        // Verify interactions
        verify(userRepository).findAllByIsDeletedTrue();
        verify(userMapper, times(2)).toDTO(any(User.class));
    }

    @Test
    void getAllDeletedUsers_ShouldReturnEmptyList_WhenNoDeletedUsersExist() {
        // Arrange
        when(userRepository.findAllByIsDeletedTrue()).thenReturn(List.of());

        // Act
        List<UserResponseDTO> result = userService.getAllDeletedUsers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAllByIsDeletedTrue();
        verifyNoInteractions(userMapper);
    }
}
