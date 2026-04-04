package com.zorvyn.assignment.FinancialRecordManagement.serviceImplTest;


import com.zorvyn.assignment.FinancialRecordManagement.constants.Category;
import com.zorvyn.assignment.FinancialRecordManagement.constants.TransactionType;
import com.zorvyn.assignment.FinancialRecordManagement.dto.FinancialRecordRequestDTO;
import com.zorvyn.assignment.FinancialRecordManagement.dto.FinancialRecordResponseDTO;
import com.zorvyn.assignment.FinancialRecordManagement.dto.PaginatedResponseDTO;
import com.zorvyn.assignment.FinancialRecordManagement.entities.FinancialRecord;
import com.zorvyn.assignment.FinancialRecordManagement.exception.BadRequestException;
import com.zorvyn.assignment.FinancialRecordManagement.exception.ResourceNotFoundException;
import com.zorvyn.assignment.FinancialRecordManagement.mapper.FinancialRecordMapper;
import com.zorvyn.assignment.FinancialRecordManagement.repository.FinancialRecordRepository;
import com.zorvyn.assignment.FinancialRecordManagement.serviceImpl.FinancialRecordServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialRecordServiceImplTest {

    @Mock
    private FinancialRecordRepository financialRecordRepository;

    @Mock
    private FinancialRecordMapper financialRecordMapper;

    @InjectMocks
    private FinancialRecordServiceImpl financialRecordService;


    @Test
    void createRecord_Success() {
        // 1. Arrange
        FinancialRecordRequestDTO requestDTO = new FinancialRecordRequestDTO();
        requestDTO.setAmount(new BigDecimal("250.00"));
        requestDTO.setDescription("Office Supplies");

        FinancialRecord initialEntity = new FinancialRecord();
        initialEntity.setAmount(new BigDecimal("250.00"));

        FinancialRecord savedEntity = new FinancialRecord();
        savedEntity.setFinancialRecordId(UUID.randomUUID().toString());
        savedEntity.setUserId("current-user-uuid");
        savedEntity.setAmount(new BigDecimal("250.00"));

        FinancialRecordResponseDTO expectedResponse = new FinancialRecordResponseDTO();
        expectedResponse.setFinancialRecordId(savedEntity.getFinancialRecordId());

        // Stubbing Mapper: Request -> Entity
        when(financialRecordMapper.toEntity(any(FinancialRecordRequestDTO.class)))
                .thenReturn(initialEntity);

        // Stubbing Repository: Save
        when(financialRecordRepository.save(any(FinancialRecord.class)))
                .thenReturn(savedEntity);

        // Stubbing Mapper: Entity -> Response
        when(financialRecordMapper.toDTO(any(FinancialRecord.class)))
                .thenReturn(expectedResponse);

        // 2. Act
        FinancialRecordResponseDTO result = financialRecordService.createRecord(requestDTO);

        // 3. Assert
        assertNotNull(result);
        assertEquals(expectedResponse.getFinancialRecordId(), result.getFinancialRecordId());

        // Verify Business Logic
        verify(financialRecordMapper).toEntity(requestDTO);
        verify(financialRecordRepository).save(argThat(record ->
                record.getFinancialRecordId() != null &&
                        "current-user-uuid".equals(record.getUserId())
        ));
        verify(financialRecordMapper).toDTO(savedEntity);
    }


    @Test
    void getRecordById_Success() {
        // 1. Arrange
        String recordId = "REC-123";
        FinancialRecord mockRecord = new FinancialRecord();
        mockRecord.setFinancialRecordId(recordId);
        mockRecord.setIsDeleted(false); // Important: Must be false for success

        FinancialRecordResponseDTO expectedDto = new FinancialRecordResponseDTO();
        expectedDto.setFinancialRecordId(recordId);

        // Stub: Repository finds the record
        when(financialRecordRepository.findByFinancialRecordId(recordId))
                .thenReturn(Optional.of(mockRecord));

        // Stub: Mapper converts it to DTO
        when(financialRecordMapper.toDTO(mockRecord)).thenReturn(expectedDto);

        // 2. Act
        FinancialRecordResponseDTO result = financialRecordService.getRecordById(recordId);

        // 3. Assert
        assertNotNull(result);
        assertEquals(recordId, result.getFinancialRecordId());
        verify(financialRecordRepository).findByFinancialRecordId(recordId);
        verify(financialRecordMapper).toDTO(mockRecord);
    }

    @Test
    void getRecordById_ThrowsException_WhenIdNotFound() {
        // 1. Arrange
        String recordId = "NON-EXISTENT";
        when(financialRecordRepository.findByFinancialRecordId(recordId))
                .thenReturn(Optional.empty());

        // 2. Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            financialRecordService.getRecordById(recordId);
        });

        assertTrue(exception.getMessage().contains("Financial record not found"));
        verifyNoInteractions(financialRecordMapper);
    }

    @Test
    void getRecordById_ThrowsException_WhenRecordIsDeleted() {
        // 1. Arrange
        String recordId = "DELETED-123";
        FinancialRecord deletedRecord = new FinancialRecord();
        deletedRecord.setFinancialRecordId(recordId);
        deletedRecord.setIsDeleted(true); // Soft-deleted record

        when(financialRecordRepository.findByFinancialRecordId(recordId))
                .thenReturn(Optional.of(deletedRecord));

        // 2. Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            financialRecordService.getRecordById(recordId);
        });

        assertEquals("This record has been deleted from the system.", exception.getMessage());
        verify(financialRecordMapper, never()).toDTO(any());
    }


    @Test
    void getAllRecords_EmptyPage() {
        // 1. Arrange
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<FinancialRecord> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(financialRecordRepository.findAllByIsDeletedFalse(pageable)).thenReturn(emptyPage);

        // 2. Act
        PaginatedResponseDTO<FinancialRecordResponseDTO> result = financialRecordService.getAllRecords(page, size);

        // 3. Assert
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalElements());
        verifyNoInteractions(financialRecordMapper);
    }


    @Test
    void filterRecords_Success() {
        // 1. Arrange
        Category category = Category.FOOD;
        TransactionType type = TransactionType.EXPENSE;
        LocalDate start = LocalDate.now().minusDays(7);
        LocalDate end = LocalDate.now();
        int page = 0;
        int size = 5;

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Create 1 mock record
        FinancialRecord record = new FinancialRecord();
        record.setFinancialRecordId("REC-FILTER-1");
        List<FinancialRecord> records = List.of(record);

        // PageImpl(List content, Pageable pageable, long total)
        Page<FinancialRecord> recordPage = new PageImpl<>(records, pageable, 1);

        when(financialRecordRepository.findWithFilters(
                eq(category), eq(type), eq(start), eq(end), eq(pageable)))
                .thenReturn(recordPage);

        // Stub the mapper
        FinancialRecordResponseDTO dto = new FinancialRecordResponseDTO();
        when(financialRecordMapper.toDTO(any(FinancialRecord.class))).thenReturn(dto);

        // 2. Act
        PaginatedResponseDTO<FinancialRecordResponseDTO> result =
                financialRecordService.filterRecords(category, type, start, end, page, size);

        // 3. Assert (Matching your DTO field names)
        assertNotNull(result);

        assertEquals(1, result.getContent().size());

        assertEquals(1L, result.getTotalElements());
        assertEquals(0, result.getPage()); // Matches your field 'page'
        assertEquals(5, result.getSize()); // Matches your field 'size'

        // Verifying interactions
        verify(financialRecordRepository).findWithFilters(category, type, start, end, pageable);
        verify(financialRecordMapper).toDTO(record);
    }


    @Test
    void updateRecord_Success() {
        // 1. Arrange
        String recordId = "REC-123";
        FinancialRecordRequestDTO request = new FinancialRecordRequestDTO();
        request.setAmount(new BigDecimal("500.00"));
        request.setDescription("Updated Description");

        FinancialRecord existingRecord = new FinancialRecord();
        existingRecord.setFinancialRecordId(recordId);
        existingRecord.setIsDeleted(false);

        FinancialRecordResponseDTO expectedResponse = new FinancialRecordResponseDTO();
        expectedResponse.setFinancialRecordId(recordId);

        // Stub: Find existing record
        when(financialRecordRepository.findByFinancialRecordId(recordId))
                .thenReturn(Optional.of(existingRecord));

        // Stub: Save the record
        when(financialRecordRepository.save(existingRecord)).thenReturn(existingRecord);

        // Stub: Convert to DTO for the return statement
        when(financialRecordMapper.toDTO(existingRecord)).thenReturn(expectedResponse);

        // 2. Act
        FinancialRecordResponseDTO result = financialRecordService.updateRecord(recordId, request);

        // 3. Assert
        assertNotNull(result);
        verify(financialRecordRepository).findByFinancialRecordId(recordId);
        // Verify the mapper actually performed the update on the existing entity
        verify(financialRecordMapper).updateEntity(existingRecord, request);
        verify(financialRecordRepository).save(existingRecord);
    }

    @Test
    void updateRecord_ThrowsException_WhenRecordIsDeleted() {
        // 1. Arrange
        String recordId = "REC-456";
        FinancialRecord deletedRecord = new FinancialRecord();
        deletedRecord.setIsDeleted(true); // Soft-deleted

        when(financialRecordRepository.findByFinancialRecordId(recordId))
                .thenReturn(Optional.of(deletedRecord));

        // 2. Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            financialRecordService.updateRecord(recordId, new FinancialRecordRequestDTO());
        });

        assertEquals("Cannot update a deleted record.", exception.getMessage());
        verify(financialRecordRepository, never()).save(any());
    }

    @Test
    void updateRecord_ThrowsException_WhenNotFound() {
        // 1. Arrange
        String recordId = "MISSING-ID";
        when(financialRecordRepository.findByFinancialRecordId(recordId))
                .thenReturn(Optional.empty());

        // 2. Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            financialRecordService.updateRecord(recordId, new FinancialRecordRequestDTO());
        });
    }

    @Test
    void deleteRecord_Success() {
        // 1. Arrange
        String recordId = "REC-999";
        FinancialRecord existingRecord = new FinancialRecord();
        existingRecord.setFinancialRecordId(recordId);
        existingRecord.setIsDeleted(false); // Initially active

        // Stub: Find the record
        when(financialRecordRepository.findByFinancialRecordId(recordId))
                .thenReturn(Optional.of(existingRecord));

        // 2. Act
        financialRecordService.deleteRecord(recordId);

        // 3. Assert
        assertTrue(existingRecord.getIsDeleted(), "The record should be marked as deleted");

        // Verify that the record was saved with the updated flag
        verify(financialRecordRepository).findByFinancialRecordId(recordId);
        verify(financialRecordRepository).save(existingRecord);
    }

    @Test
    void deleteRecord_ThrowsException_WhenNotFound() {
        // 1. Arrange
        String recordId = "MISSING-ID";
        when(financialRecordRepository.findByFinancialRecordId(recordId))
                .thenReturn(Optional.empty());

        // 2. Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            financialRecordService.deleteRecord(recordId);
        });

        assertEquals("Record not found", exception.getMessage());

        // Verify save was NEVER called because the record doesn't exist
        verify(financialRecordRepository, never()).save(any());
    }
}
