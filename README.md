# Inventory Management for Traders | TradeStock Manager 📈

> **A Java Desktop Trading & Inventory System built with Java 17, Swing GUI, JDBC, and SQLite.**  
> *A modernized rewrite of a C++ console system for local shop inventory automation and error-free stock trading.*

---

## 📌 Project Overview & CV Highlights

- **Dual-Role Access Control**: Separate secure workflows for **Admins** (stock CRUD, trader directory, order audit logs) and **Traders** (market browsing, BUY/SELL order placement, portfolio tracking).
- **Automated Business Rules**:
  - **Age Verification**: Rejects trader registration if age is under 18.
  - **Balance Guard**: Prevents BUY trades exceeding available trader balance.
  - **Inventory Guard**: Prevents BUY trades exceeding available market quantity.
  - **Ownership Guard**: Prevents SELL trades exceeding trader's net owned shares.
  - **Audit Logging**: Logs every order into SQLite with stock price, quantity, and ISO timestamp.
- **Zero Setup & Maintenance**: Auto-creates SQLite database (`tradestock.db`) and seeds initial data (`admin` / `admin123` & sample stocks) on first launch.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Java 17+ (Plain Java, Lightweight, No Heavy Frameworks)
- **GUI Framework**: Java Swing (Custom Slate Theme, Responsive Layouts, Dynamic Tables)
- **Database**: SQLite JDBC (`tradestock.db` single-file embedded database)
- **Design Pattern**: Object-Oriented Programming (OOP) & Data Access Object (DAO) Pattern

### Project Structure
```text
TRAD_INV/
├── lib/
│   ├── sqlite-jdbc-3.45.1.0.jar
│   ├── slf4j-api-2.0.9.jar
│   └── slf4j-simple-2.0.9.jar
├── src/
│   └── com/
│       └── tradestock/
│           ├── Main.java                 # Entry Point
│           ├── db/
│           │   └── DatabaseManager.java  # SQLite Driver & Auto-Schema Init
│           ├── model/
│           │   ├── Admin.java            # Admin Entity
│           │   ├── Trader.java           # Trader Entity
│           │   ├── Stock.java            # Stock Entity
│           │   └── Order.java            # Order Entity
│           ├── dao/
│           │   ├── AdminDAO.java         # Admin DB Queries
│           │   ├── TraderDAO.java        # Trader DB Queries & Registration
│           │   ├── StockDAO.java         # Stock Inventory CRUD
│           │   └── OrderDAO.java         # Transactional Trading & Audit Logs
│           ├── ui/
│           │   ├── UITheme.java          # Design Tokens & Styling Helpers
│           │   ├── LoginFrame.java       # Role Selection & Login UI
│           │   ├── RegisterTraderFrame.java # Trader Signup (18+ Age Check)
│           │   ├── AdminDashboardFrame.java  # Admin Workspace
│           │   └── TraderDashboardFrame.java # Trader Workspace & Live Trading
│           └── test/
│               └── TestApp.java          # Headless Integration Test Suite
├── compile.bat                           # CMD Build Script
├── run.bat                               # CMD Launch Script
└── README.md                             # Documentation
```

---

## 🚀 Quick Start Guide

### Prerequisites
- **Java JDK 17+** installed.

---

### Option A: From PowerShell (Recommended)

```powershell
# 1. Navigate to project root
cd C:\Users\kulka\Downloads\TRAD_INV

# 2. Compile all Java packages
javac -encoding UTF-8 -cp "lib/*;bin" -d bin src/com/tradestock/*.java src/com/tradestock/db/*.java src/com/tradestock/model/*.java src/com/tradestock/dao/*.java src/com/tradestock/ui/*.java

# 3. Launch the Application
java -cp "lib/*;bin" com.tradestock.Main
```

Alternatively, run batch scripts directly from PowerShell:
```powershell
.\compile.bat
.\run.bat
```

---

### Option B: From Command Prompt (CMD)

```cmd
compile.bat
run.bat
```

---

## 🔑 Demo Login Credentials

| Role | Username | Password | Actions Available |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` | Add/Edit/Delete Stocks, View Registered Traders, System Order Audit |
| **Trader** | *Register New* | *Your Choice* | Browse Market, Place BUY/SELL Orders, Live Balance & History |

---

## 🧪 Integration Testing

To run headless integration tests verifying DB initialization, schema creation, age rules, balance checks, and transactional order execution:

```powershell
javac -encoding UTF-8 -cp "lib/*;bin" -d bin src/com/tradestock/test/TestApp.java
java -ea -cp "lib/*;bin" com.tradestock.test.TestApp
```

---

## 📝 Resume Bullet Points (For CV/Portfolio)

### 🌟 Full-Stack / Java Developer Focus
- **Architected TradeStock Manager**, a desktop trading & inventory application in Java 17, Swing GUI, and SQLite JDBC, implementing dual-role authentication for Admins and Traders.
- **Engineered transactional trade processing** with automated business rule validation (18+ age restriction, balance limits, stock availability, and unowned share guards).
- **Implemented zero-configuration database persistence** using SQLite and the DAO pattern, featuring auto-schema creation and timestamped audit logging.

### 🏪 Shop Automation Focus
- **Developed a Java desktop stock management system** for local shop trading, replacing manual inventory paper logs with automated SQLite database tracking and real-time order auditing.
