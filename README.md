# Money_management_system_console Java + MySQL JDBC

A console-based personal finance management system built with **Java 8**, **JDBC**, and **MySQL**.  
Manage your income, expenses, budgets, accounts, and generate financial reports — all from the terminal.

---

## 📸 Preview
==================================================
M O N E Y   M A N A G E R
Java + MySQL JDBC Console App
MONEY MANAGER - MAIN MENU

Accounts & Wallets
Transactions (Income / Expense)
Budget Management
Categories & Tags
Reports & Summaries
Exit
---

## ✨ Features

- 💳 **Multi-Account Management** — CASH, BANK, CREDIT, SAVINGS, INVESTMENT
- 💸 **Income & Expense Tracking** — with automatic balance updates
- 📊 **Budget Management** — set monthly budgets per category with progress tracking
- 🏷️ **Categories & Tags** — 16 default categories, custom tags on transactions
- 📈 **Financial Reports** — monthly summary, category breakdown, yearly overview
- 🔒 **Atomic Transactions** — JDBC commit/rollback ensures data integrity
- 🛡️ **SQL Injection Prevention** — PreparedStatements used throughout

---

## 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| Java 8 | Core application logic |
| MySQL 8 | Database |
| JDBC | Database connectivity |
| Maven | Dependency management |
| Eclipse IDE | Development environment |

---

## 📁 Project Structure
money-manager-java/
├── src/
│   └── main/
│       └── java/
│           └── com/moneymanager/
│               ├── Main.java
│               ├── db/
│               │   └── DBConnection.java
│               ├── models/
│               │   ├── Account.java
│               │   ├── Category.java
│               │   ├── Transaction.java
│               │   └── Budget.java
│               ├── dao/
│               │   ├── AccountDAO.java
│               │   ├── CategoryDAO.java
│               │   ├── TagDAO.java
│               │   ├── TransactionDAO.java
│               │   ├── BudgetDAO.java
│               │   └── ReportDAO.java
│               ├── ui/
│               │   ├── AccountUI.java
│               │   ├── TransactionUI.java
│               │   ├── BudgetUI.java
│               │   ├── CategoryUI.java
│               │   └── ReportUI.java
│               └── utils/
│                   └── ConsoleUtils.java
├── sql/
│   └── schema.sql
├── pom.xml
└── README.md
---

## ⚙️ Setup & Installation

### Prerequisites
- Java 8 or higher
- MySQL 8.x
- Maven
- Eclipse IDE (optional)

### Step 1 — Clone the Repository
```bash
git clone https://github.com/YOUR_USERNAME/money-manager-java.git
cd money-manager-java
```

### Step 2 — Create the Database
```bash
mysql -u root -p < sql/schema.sql
```
Or open MySQL Workbench and run `sql/schema.sql` manually.

### Step 3 — Configure Database Credentials
Open `src/main/java/com/moneymanager/db/DBConnection.java` and update:
```java
private static final String USER     = "root";         // your MySQL username
private static final String PASSWORD = "your_password"; // your MySQL password
```

### Step 4 — Build with Maven
```bash
mvn clean package
```

### Step 5 — Run
```bash
java -cp target/MoneyManager-1.0.0.jar com.moneymanager.Main
```

---

## 🗄️ Database Schema
accounts ──────────────────────┐
categories ─────────────┐      │
↓      ↓
transactions ──── transaction_tags ──── tags
budgets ─────────────────↑
### Tables
| Table | Description |
|---|---|
| `accounts` | Stores wallets and bank accounts |
| `categories` | Income and expense categories |
| `transactions` | All income and expense records |
| `tags` | Free-form tags for transactions |
| `transaction_tags` | Many-to-many mapping |
| `budgets` | Monthly budget per category |

---

## 📊 Financial Reports

| Report | Description |
|---|---|
| Monthly Summary | Total income, expense, and net savings for any month |
| Expense by Category | Breakdown of spending per category with percentage share |
| Income by Category | Breakdown of income sources |
| Yearly Overview | Month-by-month income vs expense for any year |
| Account Balances | Current balance across all accounts |

---

## 🏗️ Architecture

This project follows the **DAO (Data Access Object)** design pattern:
UI Layer        →  AccountUI, TransactionUI, BudgetUI, CategoryUI, ReportUI
DAO Layer       →  AccountDAO, TransactionDAO, BudgetDAO, CategoryDAO, ReportDAO
Model Layer     →  Account, Transaction, Budget, Category
Database Layer  →  DBConnection (Singleton), MySQL
---

## 🔑 Key Technical Highlights

- **Atomic JDBC Transactions** — `setAutoCommit(false)` + `rollback()` used when saving transactions to ensure account balance updates never partially fail
- **DAO Pattern** — clean separation between business logic and database queries
- **PreparedStatements** — all SQL queries use parameterized statements to prevent SQL injection
- **Singleton DB Connection** — single shared `Connection` instance managed via `DBConnection` class
- **Cascade Deletes** — deleting a transaction automatically reverses the account balance
- **Budget Upsert** — `ON DUPLICATE KEY UPDATE` allows setting or updating budgets without duplicates

---

## 📝 Default Categories

**Income:** Salary, Freelance, Investment, Rental Income, Gift, Other Income

**Expense:** Food & Dining, Transport, Shopping, Utilities, Rent, Healthcare, Education, Entertainment, Travel, Other Expense

---
