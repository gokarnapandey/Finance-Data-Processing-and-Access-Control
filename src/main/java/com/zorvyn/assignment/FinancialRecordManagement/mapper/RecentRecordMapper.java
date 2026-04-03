package com.zorvyn.assignment.FinancialRecordManagement.mapper;


import com.zorvyn.assignment.FinancialRecordManagement.dto.RecentRecordDTO;
import com.zorvyn.assignment.FinancialRecordManagement.entities.FinancialRecord;
import org.springframework.stereotype.Component;

@Component
public class RecentRecordMapper {

    public  RecentRecordDTO toRecentDTO(FinancialRecord entity) {
        if (entity == null) {
            return null;
        }

        RecentRecordDTO dto = new RecentRecordDTO();

        dto.setFinancialRecordId(entity.getFinancialRecordId());


        dto.setAmount(entity.getAmount());
        dto.setTransactionType(entity.getTransactionType());
        dto.setCategory(entity.getCategory());


        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }
}
