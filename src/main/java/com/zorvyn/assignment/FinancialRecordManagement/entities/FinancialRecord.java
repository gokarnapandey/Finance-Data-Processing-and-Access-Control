package com.zorvyn.assignment.FinancialRecordManagement.entities;

import com.zorvyn.assignment.FinancialRecordManagement.constants.Category;
import com.zorvyn.assignment.FinancialRecordManagement.constants.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "financial_records")
public class FinancialRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "financial_record_id", unique = true, nullable = false, updatable = false)
    private String financialRecordId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @NotNull(message = "Enter the amount")
    @Positive(message = "Amount should be a positive number")
    private BigDecimal amount;

    @NotNull(message = "Enter the category")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @NotNull(message = "Enter the transaction type")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    private String description;

    @Column(nullable = false)
    private Boolean isDeleted = false;
}
