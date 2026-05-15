package com.moneymanager.ui;

import com.moneymanager.dao.BudgetDAO;
import com.moneymanager.dao.CategoryDAO;
import com.moneymanager.models.Budget;
import com.moneymanager.models.Category;
import com.moneymanager.utils.ConsoleUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class BudgetUI {

    private final BudgetDAO   budgetDAO = new BudgetDAO();
    private final CategoryDAO catDAO    = new CategoryDAO();

    public void menu() throws SQLException {
        while (true) {
            ConsoleUtils.header("📊  BUDGETS");
            System.out.println("  1. View budgets for a month");
            System.out.println("  2. Set / Update budget");
            System.out.println("  3. Delete budget");
            System.out.println("  0. Back");
            int choice = ConsoleUtils.promptInt("Choice", 0, 3);
            switch (choice) {
            case 1: viewBudgets(); break;
            case 2: setBudget(); break;
            case 3: deleteBudget(); break;
            case 0: return;
        }
        }
    }

    private void viewBudgets() throws SQLException {
        int month = ConsoleUtils.promptInt("Month (1-12)", 1, 12);
        int year  = ConsoleUtils.promptInt("Year", 2000, 2100);
        List<Budget> budgets = budgetDAO.findByMonthYear(month, year);
        if (budgets.isEmpty()) { ConsoleUtils.info("No budgets set for this month."); return; }

        ConsoleUtils.section("Budget Report — " + month + "/" + year);
        System.out.printf("  %-3s %-5s %-18s %12s %12s %12s  %s%n",
                "ID", "Icon", "Category", "Budget", "Spent", "Remaining", "Usage");
        System.out.println("  " + ConsoleUtils.LINE);
        for (Budget b : budgets) {
            System.out.printf("  %-3d %-5s %-18s %12,.2f %12,.2f %12,.2f  %s%n",
                    b.getId(), b.getCategoryIcon(), b.getCategoryName(),
                    b.getAmount(), b.getSpent() == null ? BigDecimal.ZERO : b.getSpent(),
                    b.getRemaining(),
                    ConsoleUtils.bar(b.getUsagePercent(), 15));
        }
    }

    private void setBudget() throws SQLException {
        ConsoleUtils.section("Set Budget");
        List<Category> cats = catDAO.findByType("EXPENSE");
        for (Category c : cats) System.out.println("  " + c);

        int catId = ConsoleUtils.promptInt("Category ID", 1, Integer.MAX_VALUE);
        Category cat = catDAO.findById(catId);
        if (cat == null) { ConsoleUtils.error("Invalid category."); return; }

        int month = ConsoleUtils.promptInt("Month (1-12)", 1, 12);
        int year  = ConsoleUtils.promptInt("Year", 2000, 2100);
        BigDecimal amount = ConsoleUtils.promptAmount("Budget Amount");

        Budget b = new Budget();
        b.setCategoryId(catId); b.setMonth(month); b.setYear(year); b.setAmount(amount);

        if (budgetDAO.upsert(b)) ConsoleUtils.success("Budget saved for " + cat.getName() + " " + month + "/" + year);
        else                     ConsoleUtils.error("Failed to save budget.");
    }

    private void deleteBudget() throws SQLException {
        int month = ConsoleUtils.promptInt("Month (1-12)", 1, 12);
        int year  = ConsoleUtils.promptInt("Year", 2000, 2100);
        viewBudgets();
        int id = ConsoleUtils.promptInt("Budget ID to delete", 1, Integer.MAX_VALUE);
        if (ConsoleUtils.confirm("Delete budget #" + id + "?")) {
            if (budgetDAO.delete(id)) ConsoleUtils.success("Deleted.");
            else                      ConsoleUtils.error("Delete failed.");
        }
    }
}
