package com.moneymanager;


/**
 * ╔══════════════════════════════════════════╗
 *   💰  Money Manager — Java + MySQL JDBC
 * ╚══════════════════════════════════════════╝
 *
 * Run steps:
 *   1. Execute sql/schema.sql in MySQL first.
 *   2. Set DB credentials in DBConnection.java.
 *   3. Compile & run:
 *        javac -cp .:mysql-connector-java.jar $(find src -name "*.java") -d out
 *        java  -cp out:mysql-connector-java.jar com.moneymanager.Main
 */


import com.moneymanager.db.DBConnection;
import com.moneymanager.ui.AccountUI;
import com.moneymanager.ui.BudgetUI;
import com.moneymanager.ui.CategoryUI;
import com.moneymanager.ui.ReportUI;
import com.moneymanager.ui.TransactionUI;
import com.moneymanager.utils.ConsoleUtils;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        printBanner();

        AccountUI     accountUI     = new AccountUI();
        TransactionUI transactionUI = new TransactionUI();
        BudgetUI      budgetUI      = new BudgetUI();
        CategoryUI    categoryUI    = new CategoryUI();
        ReportUI      reportUI      = new ReportUI();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                DBConnection.close();
            }
        }));

        boolean running = true;
        while (running) {
            ConsoleUtils.header("MONEY MANAGER - MAIN MENU");
            System.out.println("  1. Accounts & Wallets");
            System.out.println("  2. Transactions (Income / Expense)");
            System.out.println("  3. Budget Management");
            System.out.println("  4. Categories & Tags");
            System.out.println("  5. Reports & Summaries");
            System.out.println("  0. Exit");
            System.out.println();

            int choice = ConsoleUtils.promptInt("Choice", 0, 5);

            try {
                switch (choice) {
                    case 1: accountUI.menu(); break;
                    case 2: transactionUI.menu(); break;
                    case 3: budgetUI.menu(); break;
                    case 4: categoryUI.menu(); break;
                    case 5: reportUI.menu(); break;
                    case 0:
                        ConsoleUtils.info("Goodbye!");
                        running = false;
                        break;
                }
            } catch (SQLException e) {
                ConsoleUtils.error("Database error: " + e.getMessage());
            }
        }
    }

    private static void printBanner() {
        String line = buildLine(50);
        System.out.println("\n" + line);
        System.out.println("  M O N E Y   M A N A G E R");
        System.out.println("  Java + MySQL JDBC Console App");
        System.out.println(line);
    }

    private static String buildLine(int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) sb.append("=");
        return sb.toString();
    }
}