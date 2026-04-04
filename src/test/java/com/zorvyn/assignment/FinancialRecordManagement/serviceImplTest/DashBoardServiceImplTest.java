package com.zorvyn.assignment.FinancialRecordManagement.serviceImplTest;

import com.zorvyn.assignment.FinancialRecordManagement.constants.Category;
import com.zorvyn.assignment.FinancialRecordManagement.constants.TransactionType;
import com.zorvyn.assignment.FinancialRecordManagement.dto.DashboardResponseDTO;
import com.zorvyn.assignment.FinancialRecordManagement.dto.RecentRecordDTO;
import com.zorvyn.assignment.FinancialRecordManagement.entities.FinancialRecord;
import com.zorvyn.assignment.FinancialRecordManagement.mapper.RecentRecordMapper;
import com.zorvyn.assignment.FinancialRecordManagement.repository.FinancialRecordRepository;
import com.zorvyn.assignment.FinancialRecordManagement.serviceImpl.DashBoardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashBoardServiceImplTest {

    @Mock
    private FinancialRecordRepository financialRecordRepository;

    @Mock
    private RecentRecordMapper recentRecordMapper;

    @InjectMocks
    private DashBoardServiceImpl dashBoardService;

    @Test
    void getSummary_Success() {
        // 1. Arrange: Set up your test data with dates
        LocalDateTime now = LocalDateTime.now();

        FinancialRecord income = new FinancialRecord();
        income.setAmount(new BigDecimal("5000.00"));
        income.setTransactionType(TransactionType.INCOME);
        income.setCategory(Category.SALARY);
        // Using Reflection to set the private field 'createdAt' inside 'BaseEntity'
        ReflectionTestUtils.setField(income, "createdAt", now);

        FinancialRecord expense = new FinancialRecord();
        expense.setAmount(new BigDecimal("1200.00"));
        expense.setTransactionType(TransactionType.EXPENSE);
        expense.setCategory(Category.RENT);
        ReflectionTestUtils.setField(expense, "createdAt", now);
        List<FinancialRecord> mockRecords = List.of(income, expense);

        // Stub the repository
        when(financialRecordRepository.findAllByIsDeletedFalse()).thenReturn(mockRecords);

        // Stub the mapper to avoid NPEs in the Recent Activity logic
        when(recentRecordMapper.toRecentDTO(any())).thenReturn(new RecentRecordDTO());

        // 2. Act
        DashboardResponseDTO result = dashBoardService.getSummary();

        // 3. Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("5000.00"), result.getTotalIncome());
        assertEquals(new BigDecimal("1200.00"), result.getTotalExpense());
        assertEquals(new BigDecimal("3800.00"), result.getNetBalance());

        // Verify that the repository was actually called
        verify(financialRecordRepository).findAllByIsDeletedFalse();
    }

    @Test
    void getSummary_EmptyData() {
        // Arrange
        when(financialRecordRepository.findAllByIsDeletedFalse()).thenReturn(List.of());

        // Act
        DashboardResponseDTO result = dashBoardService.getSummary();

        // Assert
        assertNotNull(result);
        // Using compareTo handles potential scale differences (0 vs 0.00)
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getTotalIncome()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getTotalExpense()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getNetBalance()));
    }


    @Test
    void getCategoryWiseTotals_Success() {
        // 1. Arrange: Create records with overlapping categories to test summation
        FinancialRecord food1 = new FinancialRecord();
        food1.setAmount(new BigDecimal("100.00"));
        food1.setCategory(Category.FOOD);

        FinancialRecord food2 = new FinancialRecord();
        food2.setAmount(new BigDecimal("50.00"));
        food2.setCategory(Category.FOOD);

        FinancialRecord rent = new FinancialRecord();
        rent.setAmount(new BigDecimal("1000.00"));
        rent.setCategory(Category.RENT);

        List<FinancialRecord> mockRecords = List.of(food1, food2, rent);

        // Stub the repository
        when(financialRecordRepository.findAllByIsDeletedFalse()).thenReturn(mockRecords);

        // 2. Act
        Map<Category, BigDecimal> result = dashBoardService.getCategoryWiseTotals();

        // 3. Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // Should have two keys: FOOD and RENT

        // Verify math: FOOD (100 + 50 = 150)
        assertEquals(0, new BigDecimal("150.00").compareTo(result.get(Category.FOOD)));

        // Verify math: RENT (1000)
        assertEquals(0, new BigDecimal("1000.00").compareTo(result.get(Category.RENT)));

        verify(financialRecordRepository).findAllByIsDeletedFalse();
    }

    @Test
    void getCategoryWiseTotals_EmptyData() {
        // 1. Arrange
        when(financialRecordRepository.findAllByIsDeletedFalse()).thenReturn(List.of());

        // 2. Act
        Map<Category, BigDecimal> result = dashBoardService.getCategoryWiseTotals();

        // 3. Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void getMonthlyTrends_Success() {
        // 1. Arrange: Create records in different months
        LocalDateTime marchDate = LocalDateTime.of(2026, 3, 15, 10, 0);
        LocalDateTime aprilDate = LocalDateTime.of(2026, 4, 10, 10, 0);

        FinancialRecord rec1 = new FinancialRecord();
        rec1.setAmount(new BigDecimal("2000.00"));
        ReflectionTestUtils.setField(rec1, "createdAt", marchDate); // Set to March

        FinancialRecord rec2 = new FinancialRecord();
        rec2.setAmount(new BigDecimal("1500.00"));
        ReflectionTestUtils.setField(rec2, "createdAt", marchDate); // Also March

        FinancialRecord rec3 = new FinancialRecord();
        rec3.setAmount(new BigDecimal("3000.00"));
        ReflectionTestUtils.setField(rec3, "createdAt", aprilDate); // Set to April

        List<FinancialRecord> mockRecords = List.of(rec1, rec2, rec3);

        // Stub the repository
        when(financialRecordRepository.findAllByIsDeletedFalse()).thenReturn(mockRecords);

        // 2. Act
        Map<String, BigDecimal> result = dashBoardService.getMonthlyTrends();

        // 3. Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // March and April entries

        // Verify March Total: 2000 + 1500 = 3500.00
        // The key format depends on your logic (likely "2026-03")
        assertTrue(result.containsKey("2026-03"));
        assertEquals(0, new BigDecimal("3500.00").compareTo(result.get("2026-03")));

        // Verify April Total: 3000.00
        assertTrue(result.containsKey("2026-04"));
        assertEquals(0, new BigDecimal("3000.00").compareTo(result.get("2026-04")));

        verify(financialRecordRepository).findAllByIsDeletedFalse();
    }

    @Test
    void getMonthlyTrends_EmptyData() {
        // Arrange
        when(financialRecordRepository.findAllByIsDeletedFalse()).thenReturn(List.of());

        // Act
        Map<String, BigDecimal> result = dashBoardService.getMonthlyTrends();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getRecentActivities_Success() {
        // 1. Arrange: Create records with different timestamps
        LocalDateTime olderDate = LocalDateTime.of(2026, 4, 1, 10, 0);
        LocalDateTime newerDate = LocalDateTime.of(2026, 4, 4, 10, 0);

        FinancialRecord oldRecord = new FinancialRecord();
        oldRecord.setAmount(new BigDecimal("100.00"));
        ReflectionTestUtils.setField(oldRecord, "createdAt", olderDate);

        FinancialRecord newRecord = new FinancialRecord();
        newRecord.setAmount(new BigDecimal("500.00"));
        ReflectionTestUtils.setField(newRecord, "createdAt", newerDate);

        List<FinancialRecord> mockRecords = List.of(oldRecord, newRecord);

        // Stub Repository
        when(financialRecordRepository.findAllByIsDeletedFalse()).thenReturn(mockRecords);

        // Stub Mapper: When the service maps the record, return a mock DTO
        RecentRecordDTO mockDto = new RecentRecordDTO();
        when(recentRecordMapper.toRecentDTO(any(FinancialRecord.class))).thenReturn(mockDto);

        // 2. Act
        List<RecentRecordDTO> result = dashBoardService.getRecentActivities();

        // 3. Assert
        assertNotNull(result);
        // If your logic limits to 5, we verify we got what we provided (2 records)
        assertEquals(2, result.size());

        // Verify interactions
        verify(financialRecordRepository).findAllByIsDeletedFalse();
        // Verify mapper was called for each record
        verify(recentRecordMapper, times(2)).toRecentDTO(any(FinancialRecord.class));
    }

    @Test
    void getRecentActivities_EmptyData() {
        // Arrange
        when(financialRecordRepository.findAllByIsDeletedFalse()).thenReturn(List.of());

        // Act
        List<RecentRecordDTO> result = dashBoardService.getRecentActivities();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getSummary_VerifyCalculationLogic() {
        // 1. Arrange
        LocalDateTime now = LocalDateTime.now();

        FinancialRecord exp1 = new FinancialRecord();
        exp1.setAmount(new BigDecimal("100.50"));
        exp1.setTransactionType(TransactionType.EXPENSE);
        exp1.setCategory(Category.FOOD); // FIX: Set Category
        ReflectionTestUtils.setField(exp1, "createdAt", now);

        FinancialRecord exp2 = new FinancialRecord();
        exp2.setAmount(new BigDecimal("200.25"));
        exp2.setTransactionType(TransactionType.EXPENSE);
        exp2.setCategory(Category.RENT); // FIX: Set Category
        ReflectionTestUtils.setField(exp2, "createdAt", now);

        FinancialRecord inc1 = new FinancialRecord();
        inc1.setAmount(new BigDecimal("500.00"));
        inc1.setTransactionType(TransactionType.INCOME);
        inc1.setCategory(Category.SALARY); // FIX: Set Category
        ReflectionTestUtils.setField(inc1, "createdAt", now);

        // Stubbing
        when(financialRecordRepository.findAllByIsDeletedFalse())
                .thenReturn(List.of(exp1, exp2, inc1));

        // Stub the mapper since getSummary calls getRecentActivitiesLogic
        when(recentRecordMapper.toRecentDTO(any())).thenReturn(new RecentRecordDTO());

        // 2. Act
        DashboardResponseDTO result = dashBoardService.getSummary();

        // 3. Assert
        assertNotNull(result);
        // 100.50 + 200.25 = 300.75
        assertEquals(0, new BigDecimal("300.75").compareTo(result.getTotalExpense()));
        assertEquals(0, new BigDecimal("500.00").compareTo(result.getTotalIncome()));
        assertEquals(0, new BigDecimal("199.25").compareTo(result.getNetBalance()));
    }

    @Test
    void getCategoryTotalsLogic_ShouldSumByCategory() {
        // 1. Arrange
        FinancialRecord r1 = new FinancialRecord();
        r1.setAmount(new BigDecimal("100.00"));
        r1.setCategory(Category.FOOD);

        FinancialRecord r2 = new FinancialRecord();
        r2.setAmount(new BigDecimal("50.00"));
        r2.setCategory(Category.FOOD);

        List<FinancialRecord> mockRecords = List.of(r1, r2);

        // CRITICAL: Stub the repository to return your mock records
        when(financialRecordRepository.findAllByIsDeletedFalse()).thenReturn(mockRecords);

        // 2. Act
        Map<Category, BigDecimal> result = dashBoardService.getCategoryWiseTotals();

        // 3. Assert
        assertNotNull(result, "The result map should not be null");

        // Check if the key exists before comparing to avoid NPE in the test itself
        assertTrue(result.containsKey(Category.FOOD), "Map should contain Category.FOOD");

        // Now compare values
        assertEquals(0, new BigDecimal("150.00").compareTo(result.get(Category.FOOD)));
    }

    @Test
    void getMonthlyTrendsLogic_Success() {
        // 1. Arrange
        LocalDateTime marchDate = LocalDateTime.of(2026, 3, 1, 10, 0);
        LocalDateTime aprilDate = LocalDateTime.of(2026, 4, 1, 10, 0);

        FinancialRecord r1 = new FinancialRecord();
        r1.setAmount(new BigDecimal("1000.00"));
        ReflectionTestUtils.setField(r1, "createdAt", marchDate);

        FinancialRecord r2 = new FinancialRecord();
        r2.setAmount(new BigDecimal("500.00"));
        ReflectionTestUtils.setField(r2, "createdAt", marchDate); // Same month

        FinancialRecord r3 = new FinancialRecord();
        r3.setAmount(new BigDecimal("2000.00"));
        ReflectionTestUtils.setField(r3, "createdAt", aprilDate); // Different month

        // Stub the repository used by the public method
        when(financialRecordRepository.findAllByIsDeletedFalse())
                .thenReturn(List.of(r1, r2, r3));

        // 2. Act
        Map<String, BigDecimal> result = dashBoardService.getMonthlyTrends();

        // 3. Assert
        assertNotNull(result);
        // March Total: 1000 + 500 = 1500
        assertEquals(0, new BigDecimal("1500.00").compareTo(result.get("2026-03")));
        // April Total: 2000
        assertEquals(0, new BigDecimal("2000.00").compareTo(result.get("2026-04")));
    }

    @Test
    void getRecentActivitiesLogic_ShouldReturnOnlyTop5Newest() {
        // 1. Arrange: Create 6 records with different dates
        List<FinancialRecord> mockRecords = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            FinancialRecord record = new FinancialRecord();
            record.setAmount(new BigDecimal(i * 100));
            // Use Reflection to set dates: record 1 is oldest, record 6 is newest
            ReflectionTestUtils.setField(record, "createdAt", LocalDateTime.now().plusDays(i));
            mockRecords.add(record);
        }

        when(financialRecordRepository.findAllByIsDeletedFalse()).thenReturn(mockRecords);

        // Stub the mapper to return a DTO with the same amount so we can verify sorting
        when(recentRecordMapper.toRecentDTO(any())).thenAnswer(invocation -> {
            FinancialRecord source = invocation.getArgument(0);
            RecentRecordDTO dto = new RecentRecordDTO();
            dto.setAmount(source.getAmount());
            return dto;
        });

        // 2. Act
        List<RecentRecordDTO> result = dashBoardService.getRecentActivities();

        // 3. Assert
        assertEquals(5, result.size(), "Should limit to exactly 5 records");

        // The first item in the result should be the NEWEST (record with 600.00)
        assertEquals(0, new BigDecimal("600.00").compareTo(result.get(0).getAmount()),
                "The first record should be the most recently created one");
    }
}