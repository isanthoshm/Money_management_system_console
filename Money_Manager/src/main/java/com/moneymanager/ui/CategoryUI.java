package com.moneymanager.ui;
import java.util.Arrays;
import com.moneymanager.dao.CategoryDAO;
import com.moneymanager.dao.TagDAO;
import com.moneymanager.models.Category;
import com.moneymanager.utils.ConsoleUtils;

import java.sql.SQLException;
import java.util.List;

public class CategoryUI {

    private final CategoryDAO catDAO = new CategoryDAO();
    private final TagDAO      tagDAO = new TagDAO();

    public void menu() throws SQLException {
        while (true) {
            ConsoleUtils.header("🏷️  CATEGORIES & TAGS");
            System.out.println("  1. View all categories");
            System.out.println("  2. Add category");
            System.out.println("  3. Delete category");
            System.out.println("  4. View all tags");
            System.out.println("  0. Back");
            int choice = ConsoleUtils.promptInt("Choice", 0, 4);
            switch (choice) {
            case 1: listCategories(); break;
            case 2: addCategory(); break;
            case 3: deleteCategory(); break;
            case 4: listTags(); break;
            case 0: return;
        }
        }
    }

    private void listCategories() throws SQLException {
        ConsoleUtils.section("All Categories");
        List<Category> cats = catDAO.findAll();
        System.out.println("  --- INCOME ---");
        cats.stream().filter(c -> "INCOME".equals(c.getType()))
                .forEach(c -> System.out.println("  " + c));
        System.out.println("  --- EXPENSE ---");
        cats.stream().filter(c -> "EXPENSE".equals(c.getType()))
                .forEach(c -> System.out.println("  " + c));
    }

    private void addCategory() throws SQLException {
        ConsoleUtils.section("Add Category");
        String name = ConsoleUtils.prompt("Category name");
        if (name != null && !name.trim().isEmpty()) { ConsoleUtils.error("Name cannot be empty."); return; }
        String type = ConsoleUtils.prompt("Type (INCOME/EXPENSE)").toUpperCase();
        if (!Arrays.asList("INCOME","EXPENSE").contains(type)) { ConsoleUtils.error("Invalid type."); return; }
        String icon = ConsoleUtils.prompt("Icon (emoji, optional, default 💰)");
        if (icon != null && !icon.trim().isEmpty()) icon = "💰";

        Category c = new Category();
        c.setName(name); c.setType(type); c.setIcon(icon);
        if (catDAO.create(c)) ConsoleUtils.success("Category added!");
        else                  ConsoleUtils.error("Failed. (May already exist)");
    }

    private void deleteCategory() throws SQLException {
        listCategories();
        int id = ConsoleUtils.promptInt("Category ID to delete", 1, Integer.MAX_VALUE);
        if (ConsoleUtils.confirm("Delete category #" + id + "? (Transactions using it will be affected)")) {
            if (catDAO.delete(id)) ConsoleUtils.success("Deleted.");
            else                   ConsoleUtils.error("Delete failed.");
        }
    }

    private void listTags() throws SQLException {
        ConsoleUtils.section("All Tags");
        List<String> tags = tagDAO.findAll();
        if (tags.isEmpty()) ConsoleUtils.info("No tags yet.");
        else tags.forEach(t -> System.out.println("  🏷️  " + t));
    }
}
