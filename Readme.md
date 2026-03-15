# 💼 SmartExpenseTracker

A RESTful Expense Management API built with Spring Boot, MySQL, and Swagger.

## Tech Stack
- Java 17
- Spring Boot 3.2.3
- Spring Data JPA + Hibernate
- MySQL 8
- Swagger / OpenAPI 3 (springdoc)
- Maven

## Features
- User registration and login
- Add, edit, delete expenses
- Category-wise expense tracking (FOOD, TRAVEL, BILLS, etc.)
- Monthly budget limits per category
- Budget alert when spending exceeds limit
- Filter expenses by category and date range
- Dashboard with total spent and category breakdown

## How to Run
1. Create MySQL database: `CREATE DATABASE smart_expense_db;`
2. Update `src/main/resources/application.properties` with your MySQL password
3. Run: `mvn spring-boot:run`
4. Open Swagger UI: `http://localhost:8080/swagger-ui.html`

## API Layers
Controller → Service → Repository → Entity (DTO pattern used throughout)
