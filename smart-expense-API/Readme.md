<div align="center">

# 💰 Smart Expense Tracker API

### A production-ready REST API for personal finance management

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/JWT-Secured-000000?style=for-the-badge&logo=json-web-tokens&logoColor=white)](https://jwt.io/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![Tests](https://img.shields.io/badge/Tests-24%20Passing-brightgreen?style=for-the-badge&logo=junit5&logoColor=white)](https://junit.org/junit5/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

<br/>

**Track expenses · Set budgets · Get alerts · Export CSV reports**

[Getting Started](#-setup--run) · [API Docs](#-api-endpoints) · [Roadmap](#-roadmap) · [License](#-license)

---

</div>

## 📋 Table of Contents

- [Overview](#-overview)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Setup & Run](#-setup--run)
- [JWT Authentication Flow](#-jwt-authentication-flow)
- [API Endpoints](#-api-endpoints)
- [Dashboard Response Example](#-dashboard-response-example)
- [Security Design](#-security-design)
- [Unit Tests](#-unit-tests)
- [Roadmap](#-roadmap)
- [Author](#-author)
- [License](#-license)

---

## 🌟 Overview

**Smart Expense Tracker API** is a fully-featured, production-grade backend application built with **Spring Boot 3** that enables users to:

- 📌 **Register & authenticate** securely with JWT tokens
- 💸 **Track expenses** across customizable categories
- 📊 **Set monthly budgets** and receive intelligent overspend alerts
- 📁 **Export reports** as CSV files (by date range or category)
- 🔔 **Monitor a real-time dashboard** for spending summaries and financial insights

The API is fully documented with **Swagger UI**, tested with **24 passing unit tests**, and secured with **BCrypt + JWT (HS512)**.

---

## 🚀 Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 17 |
| Framework | Spring Boot | 3.2.3 |
| Security | Spring Security + JWT (jjwt) | 0.12.5 |
| Database | MySQL + Spring Data JPA + Hibernate | 8.0 |
| Documentation | Swagger UI (SpringDoc OpenAPI 3) | Latest |
| Build Tool | Maven | 3.8+ |
| Testing | JUnit 5 + Mockito | Latest |
| CSV Export | Apache Commons CSV | Latest |

---

## 📁 Project Structure

```
SmartExpenseTracker/
├── src/
│   ├── main/
│   │   └── java/com/smartexpense/
│   │       ├── config/
│   │       │   ├── JwtConfig.java                    # JWT properties binding
│   │       │   ├── SecurityConfig.java               # Spring Security filter chain
│   │       │   └── SwaggerConfig.java                # OpenAPI + Bearer auth setup
│   │       ├── controller/
│   │       │   ├── AuthController.java               # POST /api/auth/login
│   │       │   ├── UserController.java               # /api/users
│   │       │   ├── ExpenseController.java            # /api/expenses
│   │       │   ├── BudgetController.java             # /api/budgets
│   │       │   └── CsvExportController.java          # /api/export
│   │       ├── service/
│   │       │   ├── UserService.java
│   │       │   ├── ExpenseService.java
│   │       │   ├── BudgetService.java
│   │       │   └── CsvExportService.java
│   │       ├── security/
│   │       │   ├── JwtUtil.java                      # Token generation & validation
│   │       │   ├── JwtAuthenticationFilter.java      # Per-request filter
│   │       │   └── JwtAuthenticationEntryPoint.java  # Custom 401 handler
│   │       ├── entity/
│   │       │   ├── User.java
│   │       │   ├── Expense.java
│   │       │   └── Budget.java
│   │       ├── dto/
│   │       │   ├── UserDTO.java
│   │       │   ├── ExpenseDTO.java
│   │       │   ├── BudgetDTO.java
│   │       │   └── DashboardDTO.java
│   │       ├── repository/
│   │       │   ├── UserRepository.java
│   │       │   ├── ExpenseRepository.java
│   │       │   └── BudgetRepository.java
│   │       └── exception/
│   │           ├── GlobalExceptionHandler.java
│   │           └── ResourceNotFoundException.java
│   └── test/
│       └── java/com/smartexpense/
│           ├── controller/
│           │   └── ExpenseControllerTest.java        # WebMvcTest (3 tests)
│           └── service/
│               ├── UserServiceTest.java              # 8 tests
│               ├── ExpenseServiceTest.java           # 7 tests
│               └── BudgetServiceTest.java            # 6 tests
├── pom.xml
├── README.md
└── LICENSE
```

---

## ⚙️ Setup & Run

### Prerequisites

Ensure the following are installed on your machine:

| Requirement | Version |
|---|---|
| Java JDK | 17+ |
| MySQL | 8.0+ |
| Apache Maven | 3.8+ |

---

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/SmartExpenseTracker.git
cd SmartExpenseTracker
```

---

### 2. Create the MySQL Database

Log in to MySQL and run:

```sql
CREATE DATABASE smart_expense_tracker;
```

---

### 3. Configure `application.properties`

Open `src/main/resources/application.properties` and update with your credentials:

```properties
# ── Database ─────────────────────────────────────────────────────────────────
spring.datasource.url=jdbc:mysql://localhost:3306/smart_expense_tracker
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# ── JWT ───────────────────────────────────────────────────────────────────────
jwt.secret=SmartExpenseTrackerSecretKey2026VeryLongSecureKeyForHMACSHA256Algorithm
jwt.expiration=86400000    # 24 hours in milliseconds
```

> ⚠️ **Security Note:** Never commit real credentials or production secrets to version control. Use environment variables or a secrets manager in production.

---

### 4. Build & Run

```bash
# Install dependencies and build the project
mvn clean install

# Start the application
mvn spring-boot:run
```

The server will start on **`http://localhost:8080`** by default.

---

### 5. Explore with Swagger UI

Once the app is running, open your browser and navigate to:

```
http://localhost:8080/swagger-ui/index.html
```

All endpoints are documented and testable directly from the browser.

---

## 🔐 JWT Authentication Flow

This API uses **stateless JWT authentication**. Follow these steps to access protected endpoints:

```
┌─────────────────────────────────────────────────────────────┐
│  Step 1 → POST /api/auth/login                              │
│           Body: { "username": "...", "password": "..." }    │
│           Response: { "token": "eyJ..." }                   │
│                                                             │
│  Step 2 → Copy the token from the response                  │
│                                                             │
│  Step 3 → Click 🔒 Authorize in Swagger UI                  │
│           Paste the token → all protected APIs unlocked     │
└─────────────────────────────────────────────────────────────┘
```

### Route Visibility

| Access Level | Routes |
|---|---|
| 🌐 **Public** (no token needed) | `POST /api/users/register` · `POST /api/users/login` · `POST /api/auth/login` |
| 🔒 **Protected** (JWT required) | All `/api/expenses/**` · All `/api/budgets/**` · All `/api/users/{id}` · All `/api/export/**` |

---

## 📡 API Endpoints

### 🔑 Authentication

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/api/auth/login` | Login and receive a JWT token | 🌐 Public |
| `POST` | `/api/users/register` | Register a new user account | 🌐 Public |

---

### 👤 Users

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/api/users` | Retrieve all users | 🔒 JWT |
| `GET` | `/api/users/{id}` | Retrieve a user by ID | 🔒 JWT |
| `PUT` | `/api/users/{id}` | Update user profile | 🔒 JWT |
| `DELETE` | `/api/users/{id}` | Delete a user account | 🔒 JWT |

---

### 💸 Expenses

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/api/expenses` | Add a new expense | 🔒 JWT |
| `GET` | `/api/expenses/user/{userId}` | Get all expenses for a user | 🔒 JWT |
| `GET` | `/api/expenses/{id}` | Get a specific expense by ID | 🔒 JWT |
| `PUT` | `/api/expenses/{id}` | Update an expense | 🔒 JWT |
| `DELETE` | `/api/expenses/{id}` | Delete an expense | 🔒 JWT |
| `GET` | `/api/expenses/user/{userId}/filter` | Filter expenses by category or date | 🔒 JWT |

---

### 📊 Budgets & Dashboard

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/api/budgets` | Set a monthly category budget | 🔒 JWT |
| `GET` | `/api/budgets/user/{userId}` | Get all budgets for a user | 🔒 JWT |
| `GET` | `/api/budgets/dashboard/{userId}` | Get spending dashboard with alerts | 🔒 JWT |
| `DELETE` | `/api/budgets/{id}` | Delete a budget entry | 🔒 JWT |

---

### 📁 CSV Export

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/api/export/expenses/{userId}` | Export all expenses as CSV | 🔒 JWT |
| `GET` | `/api/export/expenses/{userId}/by-date` | Export expenses filtered by date range | 🔒 JWT |
| `GET` | `/api/export/expenses/{userId}/by-category` | Export expenses filtered by category | 🔒 JWT |

---

## 📊 Dashboard Response Example

The dashboard endpoint returns a rich summary of a user's monthly spending:

```json
{
  "totalSpentThisMonth": 500.00,
  "spentByCategory": {
    "FOOD": 500.00
  },
  "budgetByCategory": {
    "FOOD": 3000.00
  },
  "budgetAlerts": {
    "FOOD": "✅ OK — Spent ₹500 of ₹3000 budget"
  }
}
```

> Budget alert states include `✅ OK`, `⚠️ Warning` (approaching limit), and `🚨 Exceeded` (over budget).

---

## 🔒 Security Design

| Feature | Implementation |
|---|---|
| Password Storage | BCrypt hashing (one-way, salted) |
| Token Type | JWT signed with HS512 algorithm |
| Token Expiry | 24 hours (configurable via `jwt.expiration`) |
| Session Management | Fully stateless — `SessionCreationPolicy.STATELESS` |
| Unauthorized Response | Custom `401 JSON` via `JwtAuthenticationEntryPoint` |
| Request Filter | `OncePerRequestFilter` — skips public routes via `shouldNotFilter()` |

---

## 🧪 Unit Tests

Run the full test suite with:

```bash
mvn test
```

### Test Results

```
Tests run: 24, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS ✅
```

### Coverage Breakdown

| Test Class | Tests | Coverage Areas |
|---|---|---|
| `UserServiceTest` | 8 | Register, login, CRUD operations, input validation |
| `ExpenseServiceTest` | 7 | Add, get, update, delete, category/date filtering |
| `BudgetServiceTest` | 6 | Set budget, update, dashboard aggregation, alerts |
| `ExpenseControllerTest` | 3 | WebMvcTest layer with mocked JWT authentication |

---

## 🛣️ Roadmap

The following features are planned for upcoming releases:

### ✅ Version 1.0 — Current
- JWT authentication with secure BCrypt passwords
- Full CRUD for users, expenses, and budgets
- CSV export with filtering
- Dashboard with spend alerts
- 24 passing unit tests + Swagger docs

---

### 🔧 Version 1.1 — In Progress

#### 1. Recurring Expense Automation
Allow users to mark expenses as recurring (weekly/monthly) with auto-generation.
- Add `isRecurring` and `recurrenceType` fields to the `Expense` entity
- Use Spring's `@Scheduled` with cron expressions to auto-insert recurring entries

#### 2. Email Budget Alerts
Send email notifications when spending exceeds a budget threshold.
- Integrate **Spring Mail + Gmail SMTP**
- Trigger inside `BudgetService` when `spent > limit`

#### 3. Category Enum Validation
Enforce fixed, type-safe expense categories using a Java enum:
```java
public enum Category {
    FOOD, TRAVEL, BILLS, ENTERTAINMENT, SHOPPING, HEALTH, OTHER
}
```
- Replace `String category` with `@Enumerated Category` in entities

---

### 🔮 Version 1.2 — Planned

#### 4. Monthly Summary Report
Auto-generate a month-end report with total spend, savings vs budget, and biggest category.
```
GET /api/reports/summary/{userId}?month=3&year=2026
```

#### 5. Pagination & Sorting
Efficient handling of large expense histories using Spring Data's `Pageable`:
```
GET /api/expenses/user/1?page=0&size=10&sort=date,desc
```

#### 6. Global Rate Limiting
Prevent API abuse using the **Bucket4j** library — max 100 requests/minute per IP.

#### 7. Spring Boot Actuator
Expose health and metrics endpoints for production monitoring:
```properties
management.endpoints.web.exposure.include=health,info,metrics
```
```
GET http://localhost:8080/actuator/health
```

---

## 👨‍💻 Author

**Thill**

- 🔧 Backend: Spring Boot · JWT · MySQL
- 🧪 Testing: JUnit 5 · Mockito
- 📖 Docs: Swagger OpenAPI 3

---

## 📄 License

```
MIT License

Copyright (c) 2026 Thill

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

<div align="center">

Made with ❤️ using Spring Boot · Give this repo a ⭐ if you found it useful!

</div>