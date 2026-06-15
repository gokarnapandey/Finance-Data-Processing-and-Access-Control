package com.zorvyn.assignment.financechatbot.tools;

import com.zorvyn.assignment.financechatbot.client.FinanceApiClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Write tools that create, update, or soft-delete financial records.
 * Available to ADMIN only (also re-enforced by the Finance API).
 */
@Component
public class RecordWriteTools {

    private final FinanceApiClient finance;

    public RecordWriteTools(FinanceApiClient finance) {
        this.finance = finance;
    }

    @Tool(description = "Create a new financial record (income or expense). Returns the created record as JSON. "
            + "Confirm the details with the user before creating if anything is ambiguous.")
    public String createRecord(
            @ToolParam(description = "Positive transaction amount.") Double amount,
            @ToolParam(description = "Transaction type. One of: INCOME, EXPENSE.") String transactionType,
            @ToolParam(description = "Category. One of: FOOD, RENT, TRANSPORT, ENTERTAINMENT, SALARY, "
                    + "INVESTMENT, OTHER.") String category,
            @ToolParam(description = "Short description of the transaction (max 255 chars).") String description) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("amount", amount);
        body.put("transactionType", transactionType);
        body.put("category", category);
        body.put("description", description);
        return finance.createRecord(body);
    }

    @Tool(description = "Update an existing financial record by its business record id. Only the provided "
            + "fields are changed; omit a field to leave it unchanged. Returns the updated record as JSON.")
    public String updateRecord(
            @ToolParam(description = "The financial record's business id (financialRecordId).") String recordId,
            @ToolParam(required = false, description = "New amount (positive).") Double amount,
            @ToolParam(required = false, description = "New transaction type: INCOME or EXPENSE.") String transactionType,
            @ToolParam(required = false, description = "New category: FOOD, RENT, TRANSPORT, ENTERTAINMENT, "
                    + "SALARY, INVESTMENT, OTHER.") String category,
            @ToolParam(required = false, description = "New description.") String description) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (amount != null) body.put("amount", amount);
        if (transactionType != null) body.put("transactionType", transactionType);
        if (category != null) body.put("category", category);
        if (description != null) body.put("description", description);
        return finance.updateRecord(recordId, body);
    }

    @Tool(description = "Soft-delete a financial record by its business record id. The record is hidden from "
            + "normal views but retained for audit. Returns a JSON status.")
    public String deleteRecord(
            @ToolParam(description = "The financial record's business id (financialRecordId).") String recordId) {
        return finance.deleteRecord(recordId);
    }
}
