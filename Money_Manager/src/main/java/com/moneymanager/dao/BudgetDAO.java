package com.moneymanager.dao;

import com.moneymanager.db.DBConnection;
import com.moneymanager.models.Budget;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {

    private Connection conn() { return DBConnection.getConnection(); }

    public List<Budget> findByMonthYear(int month, int year) throws SQLException {
        String sql =
            "SELECT b.id, b.category_id, c.name AS cat_name, c.icon AS cat_icon, " +
            "       b.amount, b.month, b.year, " +
            "       COALESCE(SUM(t.amount),0) AS spent " +
            "FROM budgets b " +
            "JOIN categories c ON b.category_id = c.id " +
            "LEFT JOIN transactions t ON t.category_id = b.category_id " +
            "       AND t.type='EXPENSE' " +
            "       AND MONTH(t.transaction_date)=b.month " +
            "       AND YEAR(t.transaction_date)=b.year " +
            "WHERE b.month=? AND b.year=? " +
            "GROUP BY b.id, b.category_id, c.name, c.icon, b.amount, b.month, b.year " +
            "ORDER BY c.name";
        List<Budget> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public boolean upsert(Budget b) throws SQLException {
        String sql =
            "INSERT INTO budgets (category_id, amount, month, year) VALUES (?,?,?,?) " +
            "ON DUPLICATE KEY UPDATE amount=VALUES(amount)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, b.getCategoryId());
            ps.setBigDecimal(2, b.getAmount());
            ps.setInt(3, b.getMonth());
            ps.setInt(4, b.getYear());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM budgets WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Budget map(ResultSet rs) throws SQLException {
        Budget b = new Budget();
        b.setId(rs.getInt("id"));
        b.setCategoryId(rs.getInt("category_id"));
        b.setCategoryName(rs.getString("cat_name"));
        b.setCategoryIcon(rs.getString("cat_icon"));
        b.setAmount(rs.getBigDecimal("amount"));
        b.setSpent(rs.getBigDecimal("spent"));
        b.setMonth(rs.getInt("month"));
        b.setYear(rs.getInt("year"));
        return b;
    }
}
