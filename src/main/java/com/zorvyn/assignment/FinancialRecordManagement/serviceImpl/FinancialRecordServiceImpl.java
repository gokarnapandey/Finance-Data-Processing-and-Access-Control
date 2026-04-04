package com.zorvyn.assignment.FinancialRecordManagement.serviceImpl;

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
import com.zorvyn.assignment.FinancialRecordManagement.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private final FinancialRecordRepository financialRecordRepository;
    private final FinancialRecordMapper financialRecordMapper;

    @Override
    @Transactional
    public FinancialRecordResponseDTO createRecord(FinancialRecordRequestDTO request) {

        FinancialRecord record = financialRecordMapper.toEntity(request);

        record.setFinancialRecordId(UUID.randomUUID().toString());
        record.setUserId("current-user-uuid");
        FinancialRecord savedRecord = financialRecordRepository.save(record);

        return financialRecordMapper.toDTO(savedRecord);
    }

    @Override
    public FinancialRecordResponseDTO getRecordById(String recordId) {
        FinancialRecord record = financialRecordRepository.findByFinancialRecordId(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found with ID: " + recordId));

        if (Boolean.TRUE.equals(record.getIsDeleted())) {
            throw new ResourceNotFoundException("This record has been deleted from the system.");
        }

        return financialRecordMapper.toDTO(record);
    }



    @Override
    public PaginatedResponseDTO<FinancialRecordResponseDTO> getAllRecords(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<FinancialRecord> recordPage = financialRecordRepository.findAllByIsDeletedFalse(pageable);

        List<FinancialRecordResponseDTO> dtoList = recordPage.getContent().stream()
                .map(financialRecordMapper::toDTO)
                .toList();

        return new PaginatedResponseDTO<>(
                dtoList,
                recordPage.getNumber(),
                recordPage.getSize(),
                recordPage.getTotalElements(),
                recordPage.getTotalPages(),
                recordPage.isLast()
        );
    }



    @Override
    public PaginatedResponseDTO<FinancialRecordResponseDTO> filterRecords(
            Category category, TransactionType type, LocalDate startDate, LocalDate endDate,
            int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<FinancialRecord> recordPage = financialRecordRepository.findWithFilters(
                category, type, startDate, endDate, pageable);

        List<FinancialRecordResponseDTO> dtoList = recordPage.getContent().stream()
                .map(financialRecordMapper::toDTO)
                .toList();

        return new PaginatedResponseDTO<>(
                dtoList,
                recordPage.getNumber(),
                recordPage.getSize(),
                recordPage.getTotalElements(),
                recordPage.getTotalPages(),
                recordPage.isLast()
        );
    }

    @Override
    @Transactional
    public FinancialRecordResponseDTO updateRecord(String recordId, FinancialRecordRequestDTO request) {

        FinancialRecord record = financialRecordRepository.findByFinancialRecordId(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found: " + recordId));

        if (Boolean.TRUE.equals(record.getIsDeleted())) {
            throw new BadRequestException("Cannot update a deleted record.");
        }

        financialRecordMapper.updateEntity(record, request);

        FinancialRecord updatedRecord = financialRecordRepository.save(record);

        return financialRecordMapper.toDTO(updatedRecord);
    }

    @Override
    @Transactional
    public void deleteRecord(String recordId) {
        FinancialRecord record = financialRecordRepository.findByFinancialRecordId(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));
        record.setIsDeleted(true);
        financialRecordRepository.save(record);
    }
}
