package com.moneymanager.ui;

import com.moneymanager.dao.ReportDAO;
import com.moneymanager.utils.ConsoleUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ReportUI {

    private final ReportDAO dao = new ReportDAO();

    public void menu() throws SQLException {
        while (true) {
            ConsoleUtils.header("📈  REPORTS & SUMMARIES");
            System.out.println("  1. Monthly Summary");
            System.out.println("  2. Expense Breakdown by Category");
            System.out.println("  3. Income Breakdown by Category");
            System.out.println("  4. Yearly Overview");
            System.out.println("  5. Account Balances");
            System.out.println("  0. Back");
            int choice = ConsoleUtils.promptInt("Choice", 0, 5);
            switch (choice) {
            case 1: monthlySummary(); break;
            case 2: expenseBreakdown(); break;
            case 3: incomeBreakdown(); break;
            case 4: yearlyOverview(); break;
            case 5: accountBalances(); break;
            case 0: return;
        }
        }
    }

    private void monthlySummary() throws SQLException {
        LocalDate now = LocalDate.now();
        int month = ConsoleUtils.promptInt("Month (1-12)", 1, 12);
        int year  = ConsoleUtils.promptInt("Year", 2000, 2100);

        Map<String, BigDecimal> data = dao.monthlySummary(month, year);
        ConsoleUtils.section("Monthly Summary — " + month + "/" + year);

        BigDecimal income  = data.getOrDefault("INCOME",  BigDecimal.ZERO);
        BigDecimal expense = data.getOrDefault("EXPENSE", BigDecimal.ZERO);
        BigDecimal net     = income.subtract(expense);

        System.out.printf("  %-20s : %,.2f%n", "💰 Total Income",  income);
        System.out.printf("  %-20s : %,.2f%n", "💸 Total Expense", expense);
        System.out.println("  " + ConsoleUtils.LINE.substring(0, 40));
        System.out.printf("  %-20s : %,.2f%n", net.compareTo(BigDecimal.ZERO) >= 0 ? "✅ Net Savings" : "❌ Net Loss", net.abs());
    }

    private void expenseBreakdown() throws SQLException {
        int month = ConsoleUtils.promptInt("Month (1-12)", 1, 12);
        int year  = ConsoleUtils.promptInt("Year", 2000, 2100);
        List<Object[]> rows = dao.expenseByCategory(month, year);
        ConsoleUtils.section("Expense Breakdown — " + month + "/" + year);
        if (rows.isEmpty()) { ConsoleUtils.info("No expenses found."); return; }

        BigDecimal total = rows.stream()
                .map(r -> (BigDecimal) r[2]).reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.printf("  %-5s %-20s %12s %8s%n", "Icon", "Category", "Amount", "Share");
        System.out.println("  " + ConsoleUtils.LINE);
        for (Object[] r : rows) {
            double pct = total.compareTo(BigDecimal.ZERO) == 0 ? 0
                    : ((BigDecimal) r[2]).divide(total, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue();
            System.out.printf("  %-5s %-20s %12,.2f  %5.1f%%%n", r[0], r[1], r[2], pct);
        }
        System.out.println("  " + ConsoleUtils.LINE);
        System.out.printf("  %-26s %12,.2f%n", "TOTAL", total);
    }

    private void incomeBreakdown() throws SQLException {
        int month = ConsoleUtils.promptInt("Month (1-12)", 1, 12);
        int year  = ConsoleUtils.promptInt("Year", 2000, 2100);
        List<Object[]> rows = dao.incomeByCategory(month, year);
        ConsoleUtils.section("Income Breakdown — " + month + "/" + year);
        if (rows.isEmpty()) { ConsoleUtils.info("No income found."); return; }

        BigDecimal total = rows.stream()
                .map(r -> (BigDecimal) r[2]).reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.printf("  %-5s %-20s %12s%n", "Icon", "Category", "Amount");
        System.out.println("  " + ConsoleUtils.LINE);
        for (Object[] r : rows) {
            System.out.printf("  %-5s %-20s %12,.2f%n", r[0], r[1], r[2]);
        }
        System.out.println("  " + ConsoleUtils.LINE);
        System.out.printf("  %-26s %12,.2f%n", "TOTAL", total);
    }

    private void yearlyOverview() throws SQLException {
        int year = ConsoleUtils.promptInt("Year", 2000, 2100);
        List<Object[]> rows = dao.yearlyOverview(year);
        ConsoleUtils.section("Yearly Overview — " + year);
        if (rows.isEmpty()) { ConsoleUtils.info("No data for this year."); return; }

        System.out.printf("  %-5s %14s %14s %14s%n", "Month", "Income", "Expense", "Net");
        System.out.println("  " + ConsoleUtils.LINE);
        BigDecimal totalI = BigDecimal.ZERO, totalE = BigDecimal.ZERO;
        for (Object[] r : rows) {
            BigDecimal inc = (BigDecimal) r[1], exp = (BigDecimal) r[2];
            BigDecimal net = inc.subtract(exp);
            totalI = totalI.add(inc); totalE = totalE.add(exp);
            System.out.printf("  %-5s %14,.2f %14,.2f %14,.2f%n", r[0], inc, exp, net);
        }
        System.out.println("  " + ConsoleUtils.LINE);
        System.out.printf("  %-5s %14,.2f %14,.2f %14,.2f%n", "TOTAL", totalI, totalE, totalI.subtract(totalE));
    }

    private void accountBalances() throws SQLException {
        ConsoleUtils.section("Account Balances");
        List<Object[]> rows = dao.accountBalances();
        if (rows.isEmpty()) { ConsoleUtils.info("No accounts."); return; }
        System.out.printf("  %-22s %-12s %14s%n", "Account", "Type", "Balance");
        System.out.println("  " + ConsoleUtils.LINE);
        for (Object[] r : rows) {
            System.out.printf("  %-22s %-12s %s %14,.2f%n", r[0], r[1], r[3], r[2]);
        }
    }
}
