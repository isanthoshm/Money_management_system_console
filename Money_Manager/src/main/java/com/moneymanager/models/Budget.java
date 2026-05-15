package com.moneymanager.models;

import java.math.BigDecimal;

public class Budget {
    private int id;
    private int categoryId;
    private String categoryName;
    private String categoryIcon;
    private BigDecimal amount;
    private BigDecimal spent;
    private int month;
    private int year;

    public Budget() {}

    public int getId()                    { return id; }
    public void setId(int id)             { this.id = id; }
    public int getCategoryId()            { return categoryId; }
    public void setCategoryId(int c)      { this.categoryId = c; }
    public String getCategoryName()       { return categoryName; }
    public void setCategoryName(String c) { this.categoryName = c; }
    public String getCategoryIcon()       { return categoryIcon; }
    public void setCategoryIcon(String i) { this.categoryIcon = i; }
    public BigDecimal getAmount()         { return amount; }
    public void setAmount(BigDecimal a)   { this.amount = a; }
    public BigDecimal getSpent()          { return spent; }
    public void setSpent(BigDecimal s)    { this.spent = s; }
    public int getMonth()                 { return month; }
    public void setMonth(int m)           { this.month = m; }
    public int getYear()                  { return year; }
    public void setYear(int y)            { this.year = y; }

    public BigDecimal getRemaining() {
        if (spent == null) return amount;
        return amount.subtract(spent);
    }

    public double getUsagePercent() {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) return 0;
        if (spent == null) return 0;
        return spent.divide(amount, 4, java.math.RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
    }
}
