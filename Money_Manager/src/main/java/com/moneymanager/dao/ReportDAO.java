package com.moneymanager.dao;

import com.moneymanager.db.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class ReportDAO {

    private Connection conn() { return DBConnection.getConnection(); }

    /** Total income and expense for a given month/year */
    public Map<String, BigDecimal> monthlySummary(int month, int year) throws SQLException {
        String sql =
            "SELECT type, SUM(amount) AS total FROM transactions " +
            "WHERE MONTH(transaction_date)=? AND YEAR(transaction_date)=? " +
            "GROUP BY type";
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, month); ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) map.put(rs.getString("type"), rs.getBigDecimal("total"));
            }
        }
        return map;
    }

    /** Expense breakdown by category for a given month/year */
    public List<Object[]> expenseByCategory(int month, int year) throws SQLException {
        String sql =
            "SELECT c.icon, c.name, SUM(t.amount) AS total " +
            "FROM transactions t JOIN categories c ON t.category_id=c.id " +
            "WHERE t.type='EXPENSE' AND MONTH(t.transaction_date)=? AND YEAR(t.transaction_date)=? " +
            "GROUP BY c.id, c.icon, c.name ORDER BY total DESC";
        List<Object[]> rows = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, month); ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) rows.add(new Object[]{rs.getString(1), rs.getString(2), rs.getBigDecimal(3)});
            }
        }
        return rows;
    }

    /** Income breakdown by category for a given month/year */
    public List<Object[]> incomeByCategory(int month, int year) throws SQLException {
        String sql =
            "SELECT c.icon, c.name, SUM(t.amount) AS total " +
            "FROM transactions t JOIN categories c ON t.category_id=c.id " +
            "WHERE t.type='INCOME' AND MONTH(t.transaction_date)=? AND YEAR(t.transaction_date)=? " +
            "GROUP BY c.id, c.icon, c.name ORDER BY total DESC";
        List<Object[]> rows = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, month); ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) rows.add(new Object[]{rs.getString(1), rs.getString(2), rs.getBigDecimal(3)});
            }
        }
        return rows;
    }

    /** Month-by-month summary for a year */
    public List<Object[]> yearlyOverview(int year) throws SQLException {
        String sql =
            "SELECT MONTH(transaction_date) AS mon, type, SUM(amount) AS total " +
            "FROM transactions WHERE YEAR(transaction_date)=? " +
            "GROUP BY mon, type ORDER BY mon";
        Map<Integer, BigDecimal[]> byMonth = new TreeMap<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int m = rs.getInt("mon");
                    byMonth.putIfAbsent(m, new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
                    BigDecimal[] arr = byMonth.get(m);
                    if ("INCOME".equals(rs.getString("type"))) arr[0] = rs.getBigDecimal("total");
                    else                                         arr[1] = rs.getBigDecimal("total");
                }
            }
        }
        List<Object[]> result = new ArrayList<>();
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        for (Map.Entry<Integer, BigDecimal[]> e : byMonth.entrySet()) {
            result.add(new Object[]{months[e.getKey()-1], e.getValue()[0], e.getValue()[1]});
        }
        return result;
    }

    /** Account balances */
    public List<Object[]> accountBalances() throws SQLException {
        String sql = "SELECT name, type, balance, currency FROM accounts ORDER BY name";
        List<Object[]> rows = new ArrayList<>();
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) rows.add(new Object[]{rs.getString(1), rs.getString(2), rs.getBigDecimal(3), rs.getString(4)});
        }
        return rows;
    }
}
