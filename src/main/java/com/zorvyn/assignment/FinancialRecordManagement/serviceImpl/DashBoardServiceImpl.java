package com.zorvyn.assignment.FinancialRecordManagement.serviceImpl;

import com.zorvyn.assignment.FinancialRecordManagement.constants.Category;
import com.zorvyn.assignment.FinancialRecordManagement.constants.TransactionType;
import com.zorvyn.assignment.FinancialRecordManagement.dto.DashboardResponseDTO;
import com.zorvyn.assignment.FinancialRecordManagement.dto.RecentRecordDTO;
import com.zorvyn.assignment.FinancialRecordManagement.entities.FinancialRecord;
import com.zorvyn.assignment.FinancialRecordManagement.mapper.RecentRecordMapper;
import com.zorvyn.assignment.FinancialRecordManagement.repository.FinancialRecordRepository;
import com.zorvyn.assignment.FinancialRecordManagement.service.DashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashBoardServiceImpl implements DashBoardService {

    private final FinancialRecordRepository financialRecordRepository;
    private final RecentRecordMapper recentRecordMapper;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponseDTO getSummary() {
        // Fetch all active records once to avoid multiple DB hits
        List<FinancialRecord> allRecords = financialRecordRepository.findAllByIsDeletedFalse();

        DashboardResponseDTO response = new DashboardResponseDTO();

        // 1. Calculate Summary Totals
        BigDecimal totalIncome = calculateTotalByType(allRecords, TransactionType.INCOME);
        BigDecimal totalExpense = calculateTotalByType(allRecords, TransactionType.EXPENSE);

        response.setTotalIncome(totalIncome);
        response.setTotalExpense(totalExpense);
        response.setNetBalance(totalIncome.subtract(totalExpense));

        // 2. Map Category Totals
        response.setCategoryWiseTotals(getCategoryTotalsLogic(allRecords));

        // 3. Map Monthly Trends
        response.setMonthlyTrends(getMonthlyTrendsLogic(allRecords));

        // 4. Map Recent Activities
        response.setRecentRecords(getRecentActivitiesLogic(allRecords));

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Category, BigDecimal> getCategoryWiseTotals() {
        List<FinancialRecord> allRecords = financialRecordRepository.findAllByIsDeletedFalse();
        return getCategoryTotalsLogic(allRecords);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getMonthlyTrends() {
        List<FinancialRecord> allRecords = financialRecordRepository.findAllByIsDeletedFalse();
        return getMonthlyTrendsLogic(allRecords);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecentRecordDTO> getRecentActivities() {
        List<FinancialRecord> allRecords = financialRecordRepository.findAllByIsDeletedFalse();
        return getRecentActivitiesLogic(allRecords);
    }

    // --- Private Helper Methods (The "Engine" of the Service) ---

    private BigDecimal calculateTotalByType(List<FinancialRecord> records, TransactionType type) {
        return records.stream()
                .filter(r -> r.getTransactionType() == type)
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Map<Category, BigDecimal> getCategoryTotalsLogic(List<FinancialRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        FinancialRecord::getCategory,
                        Collectors.mapping(FinancialRecord::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
    }

    private Map<String, BigDecimal> getMonthlyTrendsLogic(List<FinancialRecord> records) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getCreatedAt().format(formatter),
                        Collectors.mapping(FinancialRecord::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
    }

    private List<RecentRecordDTO> getRecentActivitiesLogic(List<FinancialRecord> records) {
        return records.stream()
                .sorted(Comparator.comparing(FinancialRecord::getCreatedAt).reversed())
                .limit(5)
                .map(recentRecordMapper::toRecentDTO) // Using the static mapper we created
                .toList();
    }
}