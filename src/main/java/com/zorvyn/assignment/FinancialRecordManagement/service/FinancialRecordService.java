package com.zorvyn.assignment.FinancialRecordManagement.service;


import com.zorvyn.assignment.FinancialRecordManagement.constants.Category;
import com.zorvyn.assignment.FinancialRecordManagement.constants.TransactionType;
import com.zorvyn.assignment.FinancialRecordManagement.dto.FinancialRecordRequestDTO;
import com.zorvyn.assignment.FinancialRecordManagement.dto.FinancialRecordResponseDTO;
import com.zorvyn.assignment.FinancialRecordManagement.dto.PaginatedResponseDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface FinancialRecordService {

    FinancialRecordResponseDTO createRecord(FinancialRecordRequestDTO request);

    FinancialRecordResponseDTO getRecordById(String recordId);

    PaginatedResponseDTO<FinancialRecordResponseDTO> getAllRecords(int page, int size);

    PaginatedResponseDTO<FinancialRecordResponseDTO> filterRecords(
            Category category,
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size
    );

    FinancialRecordResponseDTO updateRecord(String recordId, FinancialRecordRequestDTO request);

    void deleteRecord(String recordId);
}
