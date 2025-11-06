# Agile Requirements Engineering Tool

A simple and clean authentication system built with JavaFX and Spring Boot.

## Features

- **Clean Login & Signup Pages** - Modern, minimalist design
- **Social Login Buttons** - Logo-only buttons for Google, GitHub, Microsoft, and Yahoo (OAuth integration coming soon)
- **Secure Authentication** - BCrypt password encryption
- **REST API Backend** - Spring Boot with MySQL database

## Tech Stack

- **Language:** Java 21
- **Frontend:** JavaFX 21 (with FXML)
- **Backend:** Spring Boot 3.4.11
- **Database:** MySQL 9.5
- **Security:** Spring Security with BCrypt
- **Build Tool:** Maven 3.9.11

## Prerequisites

- Java 21 (OpenJDK)
- Maven 3.9+
- MySQL 9.5+

## Database Setup

1. Start MySQL server:
```bash
brew services start mysql
```

2. Login to MySQL and create database:
```bash
mysql -u root -p
CREATE DATABASE agile_requirements_db;
```

3. Update credentials in `src/main/resources/application.properties` if needed

## Running the Application

1. Clone the repository
2. Navigate to project directory
3. Run the application:
```bash
mvn javafx:run
```

The backend API will start on `http://localhost:8080` and the JavaFX desktop window will open automatically.

## Project Structure

```
src/main/java/com/agile/requirements/
├── api/              # REST API controllers
├── config/           # Security & JavaFX configuration
├── controller/       # JavaFX controllers
├── dto/              # Data transfer objects
├── model/            # JPA entities
├── repository/       # Database repositories
├── service/          # Business logic
└── view/             # FXML view enum

src/main/resources/
├── fxml/             # JavaFX FXML files
└── application.properties
```

## API Endpoints

- `POST /api/auth/signup` - Register new user
- `POST /api/auth/login` - Authenticate user
- `GET /api/auth/check-username/{username}` - Check username availability
- `GET /api/auth/check-email/{email}` - Check email availability

## Social Login

Currently, the application displays social login buttons (Google, GitHub, Microsoft, Yahoo) that show a "coming soon" message. To implement full OAuth functionality, refer to OAuth provider documentation for integration steps.