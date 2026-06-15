package com.zorvyn.assignment.financechatbot.tools;

import com.zorvyn.assignment.financechatbot.client.FinanceApiClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * Read-only analytics tools. Available to every role (VIEWER, ANALYST, ADMIN).
 */
@Component
public class DashboardTools {

    private final FinanceApiClient finance;

    public DashboardTools(FinanceApiClient finance) {
        this.finance = finance;
    }

    @Tool(description = "Get the overall financial summary as JSON: total income, total expense, "
            + "net balance, category-wise totals, monthly trends, and the most recent records. "
            + "Use this for questions about overall balance, income vs expense, or a financial overview.")
    public String getFinancialSummary() {
        return finance.dashboardSummary();
    }

    @Tool(description = "Get total amounts grouped by category (FOOD, RENT, TRANSPORT, ENTERTAINMENT, "
            + "SALARY, INVESTMENT, OTHER) as JSON. Use for category breakdowns or 'how much on X'.")
    public String getCategoryTotals() {
        return finance.categoryTotals();
    }

    @Tool(description = "Get monthly financial trends as JSON, mapping each month (yyyy-MM) to a total. "
            + "Use for questions about spending or income over time / month by month.")
    public String getMonthlyTrends() {
        return finance.monthlyTrends();
    }

    @Tool(description = "Get the 5 most recent financial transactions as JSON. "
            + "Use for 'latest', 'recent activity', or 'last transactions' questions.")
    public String getRecentActivities() {
        return finance.recentActivities();
    }
}
