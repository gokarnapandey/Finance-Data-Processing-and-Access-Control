package com.zorvyn.assignment.financechatbot.tools;

import com.zorvyn.assignment.financechatbot.client.FinanceApiClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * Read tools over individual financial records. Available to ANALYST and ADMIN.
 */
@Component
public class RecordReadTools {

    private final FinanceApiClient finance;

    public RecordReadTools(FinanceApiClient finance) {
        this.finance = finance;
    }

    @Tool(description = "List financial records (paginated, newest first) as JSON. "
            + "Returns the requested page of active records.")
    public String listRecords(
            @ToolParam(required = false, description = "Zero-based page index. Defaults to 0.") Integer page,
            @ToolParam(required = false, description = "Page size. Defaults to 10.") Integer size) {
        return finance.listRecords(page == null ? 0 : page, size == null ? 10 : size);
    }

    @Tool(description = "Get a single financial record by its business record id (e.g. a UUID) as JSON.")
    public String getRecordById(
            @ToolParam(description = "The financial record's business id (financialRecordId).") String recordId) {
        return finance.getRecord(recordId);
    }

    @Tool(description = "Filter/search financial records by category, transaction type, and/or a date range "
            + "(paginated) and return JSON. All filters are optional; omit a filter to ignore it.")
    public String filterRecords(
            @ToolParam(required = false, description = "Category filter. One of: FOOD, RENT, TRANSPORT, "
                    + "ENTERTAINMENT, SALARY, INVESTMENT, OTHER.") String category,
            @ToolParam(required = false, description = "Transaction type filter. One of: INCOME, EXPENSE.") String type,
            @ToolParam(required = false, description = "Inclusive start date, format yyyy-MM-dd.") String startDate,
            @ToolParam(required = false, description = "Inclusive end date, format yyyy-MM-dd.") String endDate,
            @ToolParam(required = false, description = "Zero-based page index. Defaults to 0.") Integer page,
            @ToolParam(required = false, description = "Page size. Defaults to 10.") Integer size) {
        return finance.filterRecords(category, type, startDate, endDate,
                page == null ? 0 : page, size == null ? 10 : size);
    }
}
