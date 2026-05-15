package com.moneymanager.ui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.moneymanager.dao.AccountDAO;
import com.moneymanager.dao.CategoryDAO;
import com.moneymanager.dao.TransactionDAO;
import com.moneymanager.models.Account;
import com.moneymanager.models.Category;
import com.moneymanager.models.Transaction;
import com.moneymanager.utils.ConsoleUtils;

public class TransactionUI {

    private final TransactionDAO txDao  = new TransactionDAO();
    private final AccountDAO     accDao = new AccountDAO();
    private final CategoryDAO    catDao = new CategoryDAO();

    public void menu() throws SQLException {
        while (true) {
            ConsoleUtils.header("💸  TRANSACTIONS");
            System.out.println("  1. Add Income");
            System.out.println("  2. Add Expense");
            System.out.println("  3. View Transactions");
            System.out.println("  4. Delete Transaction");
            System.out.println("  0. Back");
            int choice = ConsoleUtils.promptInt("Choice", 0, 4);
            switch (choice) {
                case 1: addTransaction("INCOME"); break;
                case 2: addTransaction("EXPENSE"); break;
                case 3: viewTransactions(); break;
                case 4: deleteTransaction(); break;
                case 0: return;
            }
        }
    }

    private void addTransaction(String type) throws SQLException {
        ConsoleUtils.section("Add " + type);

        // Select account
        List<Account> accounts = accDao.findAll();
        if (accounts.isEmpty()) {
            ConsoleUtils.error("No accounts found. Add an account first.");
            return;
        }
        System.out.println("  --- Accounts ---");
        for (Account a : accounts) System.out.println("  " + a);
        int accId = ConsoleUtils.promptInt("Account ID", 1, Integer.MAX_VALUE);
        Account acc = accDao.findById(accId);
        if (acc == null) { ConsoleUtils.error("Invalid account."); return; }

        // Select category
        List<Category> cats = catDao.findByType(type);
        System.out.println("\n  --- " + type + " Categories ---");
        for (Category c : cats) System.out.println("  " + c);
        int catId = ConsoleUtils.promptInt("Category ID", 1, Integer.MAX_VALUE);
        Category cat = catDao.findById(catId);
        if (cat == null || !cat.getType().equals(type)) {
            ConsoleUtils.error("Invalid category.");
            return;
        }

        // Amount & details
        java.math.BigDecimal amount = ConsoleUtils.promptAmount("Amount");
        String desc    = ConsoleUtils.prompt("Description (optional)");
        LocalDate date = ConsoleUtils.promptDate("Date");
        String tagsRaw = ConsoleUtils.prompt("Tags (comma-separated, optional)");

        List<String> tags = tagsRaw.trim().isEmpty()
                ? new ArrayList<>()
                : Arrays.asList(tagsRaw.split(","));

        Transaction tx = new Transaction();
        tx.setAccountId(accId);
        tx.setCategoryId(catId);
        tx.setType(type);
        tx.setAmount(amount);
        tx.setDescription(desc.trim().isEmpty() ? null : desc);
        tx.setTransactionDate(date);

        if (txDao.create(tx, tags)) ConsoleUtils.success("Transaction saved!");
        else                        ConsoleUtils.error("Failed to save transaction.");
    }

    private void viewTransactions() throws SQLException {
        ConsoleUtils.section("View Transactions");
        System.out.println("  Filter options:");
        String accStr   = ConsoleUtils.prompt("Account ID (blank=all)");
        String typeStr  = ConsoleUtils.prompt("Type INCOME/EXPENSE (blank=all)");
        String monthStr = ConsoleUtils.prompt("Month 1-12 (blank=all)");
        String yearStr  = ConsoleUtils.prompt("Year e.g. 2025 (blank=all)");

        Integer accId = accStr.trim().isEmpty()   ? null : Integer.parseInt(accStr);
        String  type  = typeStr.trim().isEmpty()  ? null : typeStr.toUpperCase();
        Integer month = monthStr.trim().isEmpty() ? null : Integer.parseInt(monthStr);
        Integer year  = yearStr.trim().isEmpty()  ? null : Integer.parseInt(yearStr);

        List<Transaction> txs = txDao.findAll(accId, type, month, year);
        if (txs.isEmpty()) { ConsoleUtils.info("No transactions found."); return; }

        System.out.println();
        for (Transaction tx : txs) System.out.println("  " + tx);
        System.out.println(ConsoleUtils.LINE);
        System.out.println("  Total records: " + txs.size());
    }

    private void deleteTransaction() throws SQLException {
        ConsoleUtils.section("Delete Transaction");
        viewTransactions();
        int id = ConsoleUtils.promptInt("Transaction ID to delete", 1, Integer.MAX_VALUE);
        Transaction tx = txDao.findById(id);
        if (tx == null) { ConsoleUtils.error("Not found."); return; }
        System.out.println("  " + tx);
        if (ConsoleUtils.confirm("Delete this transaction?")) {
            if (txDao.delete(id)) ConsoleUtils.success("Deleted. Account balance reversed.");
            else                  ConsoleUtils.error("Delete failed.");
        }
    }
}