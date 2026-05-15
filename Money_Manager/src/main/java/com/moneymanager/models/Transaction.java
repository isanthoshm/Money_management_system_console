package com.moneymanager.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Transaction {
    private int id;
    private int accountId;
    private String accountName;
    private int categoryId;
    private String categoryName;
    private String categoryIcon;
    private String type;          // INCOME | EXPENSE | TRANSFER
    private BigDecimal amount;
    private String description;
    private LocalDate transactionDate;
    private List<String> tags;

    public Transaction() {}

    // Getters & Setters
    public int getId()                        { return id; }
    public void setId(int id)                 { this.id = id; }
    public int getAccountId()                 { return accountId; }
    public void setAccountId(int a)           { this.accountId = a; }
    public String getAccountName()            { return accountName; }
    public void setAccountName(String a)      { this.accountName = a; }
    public int getCategoryId()                { return categoryId; }
    public void setCategoryId(int c)          { this.categoryId = c; }
    public String getCategoryName()           { return categoryName; }
    public void setCategoryName(String c)     { this.categoryName = c; }
    public String getCategoryIcon()           { return categoryIcon; }
    public void setCategoryIcon(String i)     { this.categoryIcon = i; }
    public String getType()                   { return type; }
    public void setType(String t)             { this.type = t; }
    public BigDecimal getAmount()             { return amount; }
    public void setAmount(BigDecimal a)       { this.amount = a; }
    public String getDescription()            { return description; }
    public void setDescription(String d)      { this.description = d; }
    public LocalDate getTransactionDate()     { return transactionDate; }
    public void setTransactionDate(LocalDate d){ this.transactionDate = d; }
    public List<String> getTags()             { return tags; }
    public void setTags(List<String> t)       { this.tags = t; }

    @Override
    public String toString() {
        String tagsStr = (tags != null && !tags.isEmpty()) ? " [" + String.join(", ", tags) + "]" : "";
        return String.format("[%d] %s %-12s | %s %-18s | %s %10,.2f | %-25s | %s%s",
                id, categoryIcon, categoryName, "📂", accountName, type.equals("INCOME") ? "+" : "-",
                amount, (description != null ? description : ""), transactionDate, tagsStr);
    }
}
