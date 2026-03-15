<div align="center">

<h1>💰 Smart Expense Tracker</h1>

### A full-stack personal finance management application

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-17-DD0031?style=for-the-badge&logo=angular&logoColor=white)](https://angular.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/JWT-Secured-000000?style=for-the-badge&logo=json-web-tokens&logoColor=white)](https://jwt.io/)
[![Tests](https://img.shields.io/badge/Tests-24%20Passing-brightgreen?style=for-the-badge&logo=junit5&logoColor=white)](https://junit.org/junit5/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

<br/>

**Register · Authenticate · Track expenses · Manage budgets · Export reports**

[Backend Docs](#-backend--smart-expense-api) · [Frontend Docs](#-frontend--smart-expense-frontend) · [Getting Started](#-getting-started) · [License](#-license)

---

</div>

## 📋 Table of Contents

- [Overview](#-overview)
- [Repository Structure](#-repository-structure)
- [Tech Stack](#-tech-stack)
- [Backend — smart-expense-API](#-backend--smart-expense-api)
- [Frontend — smart-expense-frontend](#-frontend--smart-expense-frontend)
- [Getting Started](#-getting-started)
- [Roadmap](#️-roadmap)
- [Author](#-author)
- [License](#-license)

---

## 🌟 Overview

**Smart Expense Tracker** is a full-stack web application for managing personal finances. It features a **Spring Boot 3 REST API** backend and a **Angular 17 SPA** frontend, connected via JWT-secured HTTP calls.

Users can:

- 🔐 Register and log in with secure JWT authentication
- 💸 Add, edit, delete, and filter expenses by category and date
- 📊 Set monthly budgets per category and monitor spend vs limit
- 🔔 Receive real-time dashboard alerts when budgets are exceeded
- 📁 Export expenses as CSV files (full, by date range, or by category)

---

## 📁 Repository Structure

```
SmartExpenseTracker/
├── smart-expense-API/          # Spring Boot 3 REST API (Java 17)
│   ├── src/
│   ├── pom.xml
│   └── README.md               ← Backend-specific docs
│
├── smart-expense-frontend/     # Angular 17 SPA (TypeScript)
│   ├── src/
│   ├── package.json
│   └── README.md               ← Frontend-specific docs
│
├── README.md                   ← You are here
└── LICENSE
```

---

## 🚀 Tech Stack

### Backend
| Layer | Technology | Version |
|---|---|---|
| Language | Java | 17 |
| Framework | Spring Boot | 3.2.3 |
| Security | Spring Security + JWT (jjwt) | 0.12.5 |
| Database | MySQL + Spring Data JPA + Hibernate | 8.0 |
| API Docs | Swagger UI (SpringDoc OpenAPI 3) | Latest |
| Build | Maven | 3.8+ |
| Testing | JUnit 5 + Mockito | Latest |
| CSV Export | Apache Commons CSV | Latest |

### Frontend
| Technology | Version | Purpose |
|---|---|---|
| Angular | 17+ | SPA framework (standalone components) |
| TypeScript | 5+ | Type-safe development |
| Bootstrap | 5.3 | UI components & responsive layout |
| RxJS | 7+ | Reactive HTTP & state handling |
| Bootstrap Icons | 1.11 | Icon library |

---

## ⚙️ Backend — `smart-expense-API`

A production-ready **Spring Boot REST API** secured with JWT, fully documented with **Swagger UI**, and tested with **24 passing unit tests**.

### Key Features
- Stateless JWT authentication with BCrypt password hashing
- Full CRUD for users, expenses, and budgets
- Budget dashboard with per-category spend alerts
- CSV export with date-range and category filters
- Custom `GlobalExceptionHandler` and `401` entry point

### API Endpoints Summary

| Domain | Endpoints |
|---|---|
| 🔑 Auth | `POST /api/auth/login` · `POST /api/users/register` |
| 👤 Users | `GET /PUT /DELETE /api/users/{id}` |
| 💸 Expenses | `POST · GET · PUT · DELETE /api/expenses` + filter |
| 📊 Budgets | `POST · GET · DELETE /api/budgets` + dashboard |
| 📁 CSV Export | `GET /api/export/expenses/{userId}` + by-date + by-category |

### Authentication Flow

```
POST /api/auth/login  →  { "token": "eyJ..." }
         ↓
Copy token  →  Authorize in Swagger UI  →  All protected routes unlocked
```

### Security Design

| Feature | Implementation |
|---|---|
| Password Storage | BCrypt (one-way, salted) |
| Token Type | JWT · HS512 · 24h expiry |
| Session | Fully stateless — `STATELESS` policy |
| Filter | `OncePerRequestFilter` · skips public routes |
| Unauthorized | Custom `401 JSON` response |

### Test Coverage

```
Tests run: 24, Failures: 0, Errors: 0 — BUILD SUCCESS ✅
```

| Test Class | Tests |
|---|---|
| `UserServiceTest` | 8 |
| `ExpenseServiceTest` | 7 |
| `BudgetServiceTest` | 6 |
| `ExpenseControllerTest` | 3 |

📖 **Full backend documentation** → [`smart-expense-API/README.md`](smart-expense-API/README.md)

---

## 🖥️ Frontend — `smart-expense-frontend`

A modern **Angular 17 SPA** using standalone components, Bootstrap 5, and JWT-based auth — connected to the Spring Boot backend via `HttpClient` and a functional `AuthInterceptor`.

### Key Features
- Login & registration with JWT stored in `localStorage`
- `AuthGuard` protects all private routes
- Dashboard with category progress bars and color-coded alerts
- Expense management with live search, category, and date filters
- Budget management with month/year/category filters
- CSV export directly from the UI
- All filters are **client-side** — instant, zero extra API calls

### Pages & Routes

| Route | Page | Access |
|---|---|---|
| `/login` | Login | 🌐 Public |
| `/register` | Register | 🌐 Public |
| `/dashboard` | Spend overview + alerts | 🔒 AuthGuard |
| `/expenses` | Expense CRUD + filters + CSV | 🔒 AuthGuard |
| `/budgets` | Budget CRUD + filters | 🔒 AuthGuard |

### Dashboard Alerts

| Status | Condition |
|---|---|
| 🟢 Under budget | Spent < 80% of limit |
| 🟡 Approaching limit | Spent ≥ 80% of limit |
| 🔴 Budget exceeded | Spent > limit |

📖 **Full frontend documentation** → [`smart-expense-frontend/README.md`](smart-expense-frontend/README.md)

---

## 🏁 Getting Started

### Prerequisites

| Tool | Version |
|---|---|
| Java JDK | 17+ |
| MySQL | 8.0+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| Angular CLI | 17+ |

---

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/SmartExpenseTracker.git
cd SmartExpenseTracker
```

---

### 2. Start the Backend

```bash
cd smart-expense-API
```

Create the database:

```sql
CREATE DATABASE smart_expense_tracker;
```

Configure `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_expense_tracker
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

jwt.secret=YourLongSecureSecretKeyHere
jwt.expiration=86400000
```

Build and run:

```bash
mvn clean install
mvn spring-boot:run
```

> API live at → `http://localhost:8080`  
> Swagger UI → `http://localhost:8080/swagger-ui/index.html`

---

### 3. Start the Frontend

```bash
cd ../smart-expense-frontend
npm install
ng serve
```

> App live at → `http://localhost:4200`

---

## 🛣️ Roadmap

| Version | Feature | Status |
|---|---|---|
| **1.0** | JWT auth · Expense & budget CRUD · CSV export · Dashboard · 24 tests | ✅ Complete |
| **1.1** | Recurring expenses (`@Scheduled`) | 🔧 In Progress |
| **1.1** | Email budget alerts (Spring Mail + Gmail SMTP) | 🔧 In Progress |
| **1.1** | Category enum validation (`@Enumerated`) | 🔧 In Progress |
| **1.2** | Monthly summary report endpoint | 🔮 Planned |
| **1.2** | Pagination & sorting (`Pageable`) | 🔮 Planned |
| **1.2** | Global rate limiting (Bucket4j) | 🔮 Planned |
| **1.2** | Spring Boot Actuator health monitoring | 🔮 Planned |

---

## 👨‍💻 Author

**Thill**

- 🔧 Backend: Spring Boot · JWT · MySQL
- 🖥️ Frontend: Angular 17 · TypeScript · Bootstrap 5
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

Made with ❤️ using Spring Boot 3 · Angular 17 · MySQL · Give this repo a ⭐ if you found it useful!

</div>