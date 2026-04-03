package com.zorvyn.assignment.FinancialRecordManagement.mapper;


import com.zorvyn.assignment.FinancialRecordManagement.dto.FinancialRecordRequestDTO;
import com.zorvyn.assignment.FinancialRecordManagement.dto.FinancialRecordResponseDTO;
import com.zorvyn.assignment.FinancialRecordManagement.entities.FinancialRecord;
import com.zorvyn.assignment.FinancialRecordManagement.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class FinancialRecordMapper {

    // Request DTO → Entity
    public static FinancialRecord toEntity(FinancialRecordRequestDTO dto) {
        if (null == dto) {
            throw new BadRequestException("Financial record request data is missing.");
        }

        FinancialRecord record = new FinancialRecord();
        record.setAmount(dto.getAmount());
        record.setCategory(dto.getCategory());
        record.setDescription(dto.getDescription());
        record.setTransactionType(dto.getTransactionType());

        return record;
    }

    // Entity → Response DTO
    public static FinancialRecordResponseDTO toDTO(FinancialRecord record) {
        if (null == record) return null;

        FinancialRecordResponseDTO dto = new FinancialRecordResponseDTO();
        dto.setFinancialRecordId(record.getFinancialRecordId());
        dto.setCreateBy(record.getUserId());
        dto.setAmount(record.getAmount());
        dto.setCategory(record.getCategory());
        dto.setDescription(record.getDescription());

        // --- ADD THIS LINE ---
        dto.setTransactionType(record.getTransactionType());

        dto.setCreatedAt(record.getCreatedAt());
        dto.setUpdatedAt(record.getUpdatedAt());

        return dto;
    }

    public static void updateEntity(FinancialRecord record, FinancialRecordRequestDTO dto) {
        // FIXED: Changed != to == so it doesn't throw when data IS present
        if (dto == null) {
            throw new BadRequestException("Update request data is missing.");
        }

        if (null != dto.getAmount()) record.setAmount(dto.getAmount());
        if (null != dto.getCategory()) record.setCategory(dto.getCategory());
        if (null != dto.getTransactionType()) record.setTransactionType(dto.getTransactionType());
        if (null != dto.getDescription()) record.setDescription(dto.getDescription());
    }
}