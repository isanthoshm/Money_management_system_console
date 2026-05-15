package com.moneymanager.dao;

import com.moneymanager.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagDAO {

    private Connection conn() { return DBConnection.getConnection(); }

    /** Returns existing tag id or creates and returns new one. */
    public int getOrCreate(String name, Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT id FROM tags WHERE name=?")) {
            ps.setString(1, name.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO tags (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name.trim().toLowerCase());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Could not get or create tag: " + name);
    }

    public void linkToTransaction(int txId, int tagId, Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT IGNORE INTO transaction_tags (transaction_id, tag_id) VALUES (?,?)")) {
            ps.setInt(1, txId);
            ps.setInt(2, tagId);
            ps.executeUpdate();
        }
    }

    public List<String> getTagsForTransaction(int txId) throws SQLException {
        List<String> tags = new ArrayList<>();
        String sql = "SELECT t.name FROM tags t JOIN transaction_tags tt ON t.id=tt.tag_id WHERE tt.transaction_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, txId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tags.add(rs.getString("name"));
            }
        }
        return tags;
    }

    public List<String> findAll() throws SQLException {
        List<String> tags = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery("SELECT name FROM tags ORDER BY name")) {
            while (rs.next()) tags.add(rs.getString("name"));
        }
        return tags;
    }
}
