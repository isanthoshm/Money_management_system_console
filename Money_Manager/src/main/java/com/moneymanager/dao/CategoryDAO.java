package com.moneymanager.dao;

import com.moneymanager.db.DBConnection;
import com.moneymanager.models.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    private Connection conn() { return DBConnection.getConnection(); }

    public List<Category> findAll() throws SQLException {
        List<Category> list = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery("SELECT id,name,type,icon FROM categories ORDER BY type,name")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Category> findByType(String type) throws SQLException {
        List<Category> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT id,name,type,icon FROM categories WHERE type=? ORDER BY name")) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public Category findById(int id) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT id,name,type,icon FROM categories WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public boolean create(Category c) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement(
                "INSERT INTO categories (name,type,icon) VALUES (?,?,?)")) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getType());
            ps.setString(3, c.getIcon());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM categories WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Category map(ResultSet rs) throws SQLException {
        return new Category(rs.getInt("id"), rs.getString("name"),
                rs.getString("type"), rs.getString("icon"));
    }
}
