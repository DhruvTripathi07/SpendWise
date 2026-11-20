package com.spendwise.backend.repository;

import com.spendwise.backend.model.Expense;
import com.spendwise.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@ActiveProfiles("dev")
@Transactional
class ExpenseRepositoryPaginationIntegrationTests {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DataSource dataSource;

    private final String fixtureId = UUID.randomUUID().toString();
    private User firstUser;
    private User secondUser;
    private Expense juneTravel;
    private Expense juneFood;
    private Expense julyFoodOlderId;
    private Expense julyFoodNewerId;
    private Expense julyTravel;
    private Expense augustFood;
    private Expense secondUserExpense;

    @BeforeEach
    void verifySafeDatasourceAndCreateFixtures() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            String url = connection.getMetaData().getURL();
            assertNotNull(url, "Resolved datasource URL must not be null");
            assertTrue(
                    url.matches("^jdbc:mysql://localhost:3306/spendwise_db(?:\\?.*)?$"),
                    "Refusing to run: resolved datasource URL is not localhost:3306/spendwise_db: " + url);
            assertFalse(url.contains("prod"), "Refusing to run against a production datasource");
        }

        firstUser = createUser("pagination-first-" + fixtureId);
        secondUser = createUser("pagination-second-" + fixtureId);

        juneFood = createExpense(firstUser, "PAGINATION-" + fixtureId + "-JUNE-FOOD",
                "Food", LocalDate.of(2026, 6, 10));
        juneTravel = createExpense(firstUser, "PAGINATION-" + fixtureId + "-JUNE-TRAVEL",
                "Travel", LocalDate.of(2026, 6, 25));
        julyFoodOlderId = createExpense(firstUser, "PAGINATION-" + fixtureId + "-JULY-FOOD-A",
                "Food", LocalDate.of(2026, 7, 5));
        julyFoodNewerId = createExpense(firstUser, "PAGINATION-" + fixtureId + "-JULY-FOOD-B",
                "Food", LocalDate.of(2026, 7, 5));
        julyTravel = createExpense(firstUser, "PAGINATION-" + fixtureId + "-JULY-TRAVEL",
                "Travel", LocalDate.of(2026, 7, 20));
        augustFood = createExpense(firstUser, "PAGINATION-" + fixtureId + "-AUGUST-FOOD",
                "Food", LocalDate.of(2026, 8, 1));
        secondUserExpense = createExpense(secondUser, "PAGINATION-" + fixtureId + "-SECOND-USER",
                "Food", LocalDate.of(2026, 7, 15));
    }

    @Test
    void findAllByUserIdReturnsOnlyExpensesForSpecifiedUser() {
        Page<Expense> page = expenseRepository.findAllByUserId(
                firstUser.getId(), PageRequest.of(0, 4, expenseSort()));

        assertEquals(6, page.getTotalElements());
        assertTrue(page.getContent().stream()
                .allMatch(expense -> expense.getUser().getId() == firstUser.getId()));
        assertTrue(page.getContent().stream()
                .noneMatch(expense -> expense.getId() == secondUserExpense.getId()));
    }

    @Test
    void paginationHasCorrectBoundariesMetadataAndPartialLastPage() {
        Page<Expense> firstPage = expenseRepository.findAllByUserId(
                firstUser.getId(), PageRequest.of(0, 4, expenseSort()));
        Page<Expense> secondPage = expenseRepository.findAllByUserId(
                firstUser.getId(), PageRequest.of(1, 4, expenseSort()));
        Page<Expense> thirdPage = expenseRepository.findAllByUserId(
                firstUser.getId(), PageRequest.of(2, 4, expenseSort()));

        assertEquals(0, firstPage.getNumber());
        assertEquals(4, firstPage.getNumberOfElements());
        assertEquals(6, firstPage.getTotalElements());
        assertEquals(2, firstPage.getTotalPages());

        assertEquals(1, secondPage.getNumber());
        assertEquals(2, secondPage.getNumberOfElements());
        assertEquals(6, secondPage.getTotalElements());
        assertEquals(2, secondPage.getTotalPages());
        assertTrue(secondPage.isLast());

        assertEquals(0, thirdPage.getNumberOfElements());
        assertEquals(6, thirdPage.getTotalElements());
        assertEquals(2, thirdPage.getTotalPages());
    }

    @Test
    void expensesSortByDateDescendingAndIdDescendingForTieBreakers() {
        Page<Expense> page = expenseRepository.findAllByUserId(
                firstUser.getId(), PageRequest.of(0, 4, expenseSort()));

        List<Integer> ids = page.getContent().stream()
                .map(Expense::getId)
                .toList();
        assertEquals(List.of(
                        augustFood.getId(),
                        julyTravel.getId(),
                        julyFoodNewerId.getId(),
                        julyFoodOlderId.getId()),
                ids);

        assertTrue(julyFoodNewerId.getId() > julyFoodOlderId.getId());
    }

    @Test
    void julyFilteringUsesInclusiveStartAndExclusiveAugustStart() {
        YearMonth july = YearMonth.of(2026, 7);
        Pageable pageable = PageRequest.of(0, 4, expenseSort());

        Page<Expense> page = expenseRepository
                .findAllByUserIdAndDateGreaterThanEqualAndDateLessThan(
                        firstUser.getId(),
                        july.atDay(1),
                        july.plusMonths(1).atDay(1),
                        pageable);

        assertEquals(3, page.getTotalElements());
        assertTrue(page.getContent().stream()
                .allMatch(expense -> !expense.getDate().isBefore(july.atDay(1))
                        && expense.getDate().isBefore(july.plusMonths(1).atDay(1))));
        assertTrue(page.getContent().stream()
                .noneMatch(expense -> expense.getId() == juneFood.getId()
                        || expense.getId() == juneTravel.getId()
                        || expense.getId() == augustFood.getId()));
    }

    @Test
    void categoryFilteringReturnsOnlyRequestedCategory() {
        Page<Expense> page = expenseRepository.findAllByUserIdAndCategory(
                firstUser.getId(),
                "Food",
                PageRequest.of(0, 4, expenseSort()));

        assertEquals(4, page.getTotalElements());
        assertTrue(page.getContent().stream()
                .allMatch(expense -> "Food".equals(expense.getCategory())));
        assertTrue(page.getContent().stream()
                .noneMatch(expense -> expense.getId() == juneTravel.getId()
                        || expense.getId() == julyTravel.getId()
                        || expense.getId() == secondUserExpense.getId()));
    }

    @Test
    void combinedMonthAndCategoryFilteringUsesBothFilters() {
        YearMonth july = YearMonth.of(2026, 7);
        Page<Expense> page = expenseRepository
                .findAllByUserIdAndCategoryAndDateGreaterThanEqualAndDateLessThan(
                        firstUser.getId(),
                        "Food",
                        july.atDay(1),
                        july.plusMonths(1).atDay(1),
                        PageRequest.of(0, 4, expenseSort()));

        assertEquals(2, page.getTotalElements());
        assertEquals(List.of(julyFoodNewerId.getId(), julyFoodOlderId.getId()),
                page.getContent().stream()
                        .map(Expense::getId)
                        .toList());
    }

    private User createUser(String emailPrefix) {
        User user = new User();
        user.setName("Pagination Test User");
        user.setEmail(emailPrefix + "-" + UUID.randomUUID() + "@example.invalid");
        user.setPassword("not-a-real-password");
        return userRepository.saveAndFlush(user);
    }

    private Expense createExpense(User user, String description, String category, LocalDate date) {
        Expense expense = new Expense();
        expense.setUser(user);
        expense.setAmount(new BigDecimal("10.00"));
        expense.setCategory(category);
        expense.setDescription(description);
        expense.setPaymentMode("Test");
        expense.setDate(date);
        return expenseRepository.saveAndFlush(expense);
    }

    private Sort expenseSort() {
        return Sort.by(
                Sort.Order.desc("date"),
                Sort.Order.desc("id"));
    }
}
