package com.moneymanager.dao;

import com.moneymanager.db.DBConnection;
import com.moneymanager.models.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    private final TagDAO tagDAO = new TagDAO();
    private Connection conn() { return DBConnection.getConnection(); }

    private static final String BASE_SQL =
        "SELECT t.id, t.account_id, a.name AS account_name, " +
        "       t.category_id, c.name AS cat_name, c.icon AS cat_icon, " +
        "       t.type, t.amount, t.description, t.transaction_date " +
        "FROM transactions t " +
        "JOIN accounts a   ON t.account_id  = a.id " +
        "JOIN categories c ON t.category_id = c.id ";

    public List<Transaction> findAll(Integer accountId, String type, Integer month, Integer year) throws SQLException {
        StringBuilder sql = new StringBuilder(BASE_SQL).append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (accountId != null) { sql.append("AND t.account_id=? ");       params.add(accountId); }
        if (type      != null) { sql.append("AND t.type=? ");             params.add(type); }
        if (month     != null) { sql.append("AND MONTH(t.transaction_date)=? "); params.add(month); }
        if (year      != null) { sql.append("AND YEAR(t.transaction_date)=? ");  params.add(year); }
        sql.append("ORDER BY t.transaction_date DESC, t.id DESC");

        List<Transaction> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction tx = map(rs);
                    tx.setTags(tagDAO.getTagsForTransaction(tx.getId()));
                    list.add(tx);
                }
            }
        }
        return list;
    }

    public Transaction findById(int id) throws SQLException {
        String sql = BASE_SQL + "WHERE t.id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Transaction tx = map(rs);
                    tx.setTags(tagDAO.getTagsForTransaction(tx.getId()));
                    return tx;
                }
            }
        }
        return null;
    }

    /**
     * Creates a transaction and adjusts account balance — all in one atomic transaction.
     */
    public boolean create(Transaction tx, List<String> tags) throws SQLException {
        Connection c = conn();
        c.setAutoCommit(false);
        try {
            String sql = "INSERT INTO transactions (account_id, category_id, type, amount, description, transaction_date) VALUES (?,?,?,?,?,?)";
            int txId;
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, tx.getAccountId());
                ps.setInt(2, tx.getCategoryId());
                ps.setString(3, tx.getType());
                ps.setBigDecimal(4, tx.getAmount());
                ps.setString(5, tx.getDescription());
                ps.setDate(6, Date.valueOf(tx.getTransactionDate()));
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    txId = rs.getInt(1);
                }
            }

            // Adjust balance
            AccountDAO aDao = new AccountDAO();
            java.math.BigDecimal delta = tx.getType().equals("INCOME")
                    ? tx.getAmount() : tx.getAmount().negate();
            aDao.adjustBalance(tx.getAccountId(), delta, c);

            // Tags
            if (tags != null) {
                for (String tag : tags) {
                    if (tag != null && !tag.trim().isEmpty()) {
                        int tagId = tagDAO.getOrCreate(tag, c);
                        tagDAO.linkToTransaction(txId, tagId, c);
                    }
                }
            }

            c.commit();
            return true;
        } catch (SQLException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(true);
        }
    }

    public boolean delete(int id) throws SQLException {
        Connection c = conn();
        c.setAutoCommit(false);
        try {
            Transaction tx = findById(id);
            if (tx == null) return false;

            // Reverse balance
            AccountDAO aDao = new AccountDAO();
            java.math.BigDecimal delta = tx.getType().equals("INCOME")
                    ? tx.getAmount().negate() : tx.getAmount();
            aDao.adjustBalance(tx.getAccountId(), delta, c);

            try (PreparedStatement ps = c.prepareStatement("DELETE FROM transactions WHERE id=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            c.commit();
            return true;
        } catch (SQLException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(true);
        }
    }

    private Transaction map(ResultSet rs) throws SQLException {
        Transaction tx = new Transaction();
        tx.setId(rs.getInt("id"));
        tx.setAccountId(rs.getInt("account_id"));
        tx.setAccountName(rs.getString("account_name"));
        tx.setCategoryId(rs.getInt("category_id"));
        tx.setCategoryName(rs.getString("cat_name"));
        tx.setCategoryIcon(rs.getString("cat_icon"));
        tx.setType(rs.getString("type"));
        tx.setAmount(rs.getBigDecimal("amount"));
        tx.setDescription(rs.getString("description"));
        tx.setTransactionDate(rs.getDate("transaction_date").toLocalDate());
        return tx;
    }
}
