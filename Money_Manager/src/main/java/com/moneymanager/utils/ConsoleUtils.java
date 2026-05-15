package com.moneymanager.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class ConsoleUtils {

    private static final Scanner sc = new Scanner(System.in);
    public static final String LINE  = buildLine('-', 70);
    public static final String DLINE = buildLine('=', 70);

    private static String buildLine(char ch, int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) sb.append(ch);
        return sb.toString();
    }

    /** Expose scanner for direct use in UI classes */
    public static Scanner getScanner() {
        return sc;
    }

    public static void header(String title) {
        System.out.println("\n" + DLINE);
        System.out.printf("  %s%n", title);
        System.out.println(DLINE);
    }

    public static void section(String title) {
        System.out.println("\n" + LINE);
        System.out.println("  " + title);
        System.out.println(LINE);
    }

    public static void success(String msg) { System.out.println("  [OK]    " + msg); }
    public static void error(String msg)   { System.out.println("  [ERROR] " + msg); }
    public static void info(String msg)    { System.out.println("  [INFO]  " + msg); }
    public static void warn(String msg)    { System.out.println("  [WARN]  " + msg); }

    public static String prompt(String label) {
        System.out.print("  " + label + ": ");
        String line = sc.nextLine();
        return (line == null) ? "" : line.trim();
    }

    public static int promptInt(String label, int min, int max) {
        while (true) {
            try {
                System.out.print("  " + label + ": ");
                String line = sc.nextLine();
                if (line == null || line.trim().isEmpty()) {
                    System.out.println("  [ERROR] Please enter a number.");
                    continue;
                }
                int v = Integer.parseInt(line.trim());
                if (v >= min && v <= max) return v;
                System.out.println("  [ERROR] Enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("  [ERROR] Invalid number. Please try again.");
            }
        }
    }

    public static BigDecimal promptAmount(String label) {
        while (true) {
            try {
                System.out.print("  " + label + ": ");
                String line = sc.nextLine();
                if (line == null || line.trim().isEmpty()) {
                    System.out.println("  [ERROR] Amount cannot be empty.");
                    continue;
                }
                BigDecimal v = new BigDecimal(line.trim());
                if (v.compareTo(BigDecimal.ZERO) > 0) return v;
                System.out.println("  [ERROR] Amount must be positive.");
            } catch (NumberFormatException e) {
                System.out.println("  [ERROR] Invalid amount. Enter a number like 1000.00");
            }
        }
    }

    public static LocalDate promptDate(String label) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            try {
                System.out.print("  " + label + " (yyyy-MM-dd, blank=today): ");
                String s = sc.nextLine();
                if (s == null || s.trim().isEmpty()) return LocalDate.now();
                return LocalDate.parse(s.trim(), fmt);
            } catch (DateTimeParseException e) {
                System.out.println("  [ERROR] Invalid date. Use yyyy-MM-dd.");
            }
        }
    }

    public static boolean confirm(String msg) {
        System.out.print("  " + msg + " (y/n): ");
        String ans = sc.nextLine();
        return ans != null && ans.trim().equalsIgnoreCase("y");
    }

    public static String bar(double pct, int width) {
        int filled = (int) Math.min(pct / 100.0 * width, width);
        int empty  = width - filled;
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < filled; i++) bar.append("#");
        for (int i = 0; i < empty;  i++) bar.append("-");
        String label = pct >= 100 ? "[OVER]" : pct >= 80 ? "[WARN]" : "[OK]  ";
        return label + " [" + bar.toString() + "] " + String.format("%5.1f%%", pct);
    }

    /** No-op — kept for compatibility */
    public static void clearBuffer() {}
}