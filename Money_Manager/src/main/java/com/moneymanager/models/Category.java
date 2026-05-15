package com.moneymanager.models;

public class Category {
    private int id;
    private String name;
    private String type;   // INCOME | EXPENSE
    private String icon;

    public Category() {}

    public Category(int id, String name, String type, String icon) {
        this.id = id; this.name = name; this.type = type; this.icon = icon;
    }

    public int getId()            { return id; }
    public void setId(int id)     { this.id = id; }
    public String getName()       { return name; }
    public void setName(String n) { this.name = n; }
    public String getType()       { return type; }
    public void setType(String t) { this.type = t; }
    public String getIcon()       { return icon; }
    public void setIcon(String i) { this.icon = i; }

    @Override
    public String toString() {
        return String.format("[%d] %s %-20s (%s)", id, icon, name, type);
    }
}
