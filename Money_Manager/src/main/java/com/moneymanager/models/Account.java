package com.moneymanager.models;

import java.math.BigDecimal;

public class Account {
    private int id;
    private String name;
    private String type;
    private BigDecimal balance;
    private String currency;

    public Account() {}

    public Account(int id, String name, String type, BigDecimal balance, String currency) {
        this.id = id; this.name = name; this.type = type;
        this.balance = balance; this.currency = currency;
    }

    // Getters & Setters
    public int getId()               { return id; }
    public void setId(int id)        { this.id = id; }
    public String getName()          { return name; }
    public void setName(String n)    { this.name = n; }
    public String getType()          { return type; }
    public void setType(String t)    { this.type = t; }
    public BigDecimal getBalance()   { return balance; }
    public void setBalance(BigDecimal b) { this.balance = b; }
    public String getCurrency()      { return currency; }
    public void setCurrency(String c){ this.currency = c; }

    @Override
    public String toString() {
        return String.format("[%d] %-20s | %-10s | %s %,.2f", id, name, type, currency, balance);
    }
}
