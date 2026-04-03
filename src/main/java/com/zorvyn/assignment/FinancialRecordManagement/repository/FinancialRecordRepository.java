package com.zorvyn.assignment.FinancialRecordManagement.repository;

import com.zorvyn.assignment.FinancialRecordManagement.constants.Category;
import com.zorvyn.assignment.FinancialRecordManagement.constants.TransactionType;
import com.zorvyn.assignment.FinancialRecordManagement.entities.FinancialRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    // 1. Single Record Retrieval
    Optional<FinancialRecord> findByFinancialRecordId(String financialRecordId);

    // 2. Paginated Retrieval (Used for getAllRecords API)
    Page<FinancialRecord> findAllByIsDeletedFalse(Pageable pageable);

    // 3. FULL List Retrieval (FIX: Used for Dashboard calculation logic)
    List<FinancialRecord> findAllByIsDeletedFalse();

    // 4. Admin View for Deleted Records
    Page<FinancialRecord> findAllByIsDeletedTrue(Pageable pageable);

    // 5. Dynamic Filtering (Requirement 2)
    // Note: Fixed typo from 'FinacialRecord' to 'FinancialRecord'
    @Query("SELECT f FROM FinancialRecord f WHERE " +
            "(f.isDeleted = false) AND " +
            "(:category IS NULL OR f.category = :category) AND " +
            "(:type IS NULL OR f.transactionType = :type) AND " +
            "(:start IS NULL OR CAST(f.createdAt AS localdate) >= :start) AND " +
            "(:end IS NULL OR CAST(f.createdAt AS localdate) <= :end)")
    Page<FinancialRecord> findWithFilters(
            @Param("category") Category category,
            @Param("type") TransactionType type,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            Pageable pageable);
}