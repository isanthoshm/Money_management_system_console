package com.moneymanager.dao;

import com.moneymanager.db.DBConnection;
import com.moneymanager.models.Account;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    private Connection conn() { return DBConnection.getConnection(); }

    public List<Account> findAll() throws SQLException {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT id, name, type, balance, currency FROM accounts ORDER BY id";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Account findById(int id) throws SQLException {
        String sql = "SELECT id, name, type, balance, currency FROM accounts WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public boolean create(Account a) throws SQLException {
        String sql = "INSERT INTO accounts (name, type, balance, currency) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, a.getName());
            ps.setString(2, a.getType());
            ps.setBigDecimal(3, a.getBalance());
            ps.setString(4, a.getCurrency());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Account a) throws SQLException {
        String sql = "UPDATE accounts SET name=?, type=?, currency=? WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, a.getName());
            ps.setString(2, a.getType());
            ps.setString(3, a.getCurrency());
            ps.setInt(4, a.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM accounts WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean adjustBalance(int accountId, BigDecimal delta, Connection c) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE id=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, delta);
            ps.setInt(2, accountId);
            return ps.executeUpdate() > 0;
        }
    }

    private Account map(ResultSet rs) throws SQLException {
        return new Account(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("type"),
                rs.getBigDecimal("balance"),
                rs.getString("currency")
        );
    }
}
