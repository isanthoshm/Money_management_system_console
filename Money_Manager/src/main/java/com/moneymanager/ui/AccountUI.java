package com.moneymanager.ui;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.moneymanager.dao.AccountDAO;
import com.moneymanager.models.Account;
import com.moneymanager.utils.ConsoleUtils;

public class AccountUI {

    private final AccountDAO dao = new AccountDAO();

    public void menu() throws SQLException {
        while (true) {
            ConsoleUtils.header("ACCOUNTS / WALLETS");
            System.out.println("  1. View all accounts");
            System.out.println("  2. Add account");
            System.out.println("  3. Edit account");
            System.out.println("  4. Delete account");
            System.out.println("  0. Back");
            int choice = ConsoleUtils.promptInt("Choice", 0, 4);
            switch (choice) {
                case 1: listAll(); break;
                case 2: add(); break;
                case 3: edit(); break;
                case 4: delete(); break;
                case 0: return;
            }
        }
    }

    private void listAll() throws SQLException {
        ConsoleUtils.section("All Accounts");
        List<Account> accounts = dao.findAll();
        if (accounts.isEmpty()) {
            ConsoleUtils.info("No accounts found.");
            return;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (Account a : accounts) {
            System.out.println("  " + a);
            total = total.add(a.getBalance());
        }
        System.out.println(ConsoleUtils.LINE);
        System.out.printf("  %-34s TOTAL: %,.2f%n", "", total);
    }

    private void add() throws SQLException {
        ConsoleUtils.section("Add Account");

        // Name — loop until non-empty
        String name = "";
        while (name.trim().isEmpty()) {
            System.out.print("  Name: ");
            name = ConsoleUtils.getScanner().nextLine();
            if (name.trim().isEmpty()) {
                System.out.println("  [ERROR] Name cannot be empty.");
            }
        }

        // Type — loop until valid
        String type = "";
        String[] validTypes = {"CASH", "BANK", "CREDIT", "SAVINGS", "INVESTMENT"};
        while (true) {
            System.out.println("  Types: CASH, BANK, CREDIT, SAVINGS, INVESTMENT");
            System.out.print("  Type: ");
            type = ConsoleUtils.getScanner().nextLine().trim().toUpperCase();
            boolean valid = false;
            for (String t : validTypes) {
                if (t.equals(type)) { valid = true; break; }
            }
            if (valid) break;
            System.out.println("  [ERROR] Invalid type. Choose from: CASH, BANK, CREDIT, SAVINGS, INVESTMENT");
        }

        // Balance
        BigDecimal balance = ConsoleUtils.promptAmount("Opening Balance");

        // Currency
        System.out.print("  Currency (e.g. INR, USD) [default INR]: ");
        String currency = ConsoleUtils.getScanner().nextLine().trim().toUpperCase();
        if (currency.isEmpty()) currency = "INR";

        // Save
        Account a = new Account();
        a.setName(name.trim());
        a.setType(type);
        a.setBalance(balance);
        a.setCurrency(currency);

        if (dao.create(a)) ConsoleUtils.success("Account '" + name.trim() + "' created successfully!");
        else               ConsoleUtils.error("Failed to create account.");
    }

    private void edit() throws SQLException {
        listAll();
        int id = ConsoleUtils.promptInt("Enter Account ID to edit", 1, Integer.MAX_VALUE);
        Account a = dao.findById(id);
        if (a == null) {
            ConsoleUtils.error("Account not found.");
            return;
        }

        ConsoleUtils.section("Edit Account [" + a.getName() + "]");

        System.out.print("  New name (blank = keep '" + a.getName() + "'): ");
        String name = ConsoleUtils.getScanner().nextLine().trim();
        if (!name.isEmpty()) a.setName(name);

        System.out.print("  New currency (blank = keep '" + a.getCurrency() + "'): ");
        String currency = ConsoleUtils.getScanner().nextLine().trim().toUpperCase();
        if (!currency.isEmpty()) a.setCurrency(currency);

        if (dao.update(a)) ConsoleUtils.success("Account updated!");
        else               ConsoleUtils.error("Update failed.");
    }

    private void delete() throws SQLException {
        listAll();
        int id = ConsoleUtils.promptInt("Enter Account ID to delete", 1, Integer.MAX_VALUE);
        Account a = dao.findById(id);
        if (a == null) {
            ConsoleUtils.error("Account not found.");
            return;
        }
        if (ConsoleUtils.confirm("Delete '" + a.getName() + "'?")) {
            if (dao.delete(id)) ConsoleUtils.success("Deleted.");
            else                ConsoleUtils.error("Delete failed.");
        }
    }
}