# IdeaBoard - Full Stack Application
## Sprint Commit 2 - JavaFX + Spring Boot + MySQL Integration

A full-stack idea management application built with JavaFX frontend and Spring Boot backend, connected to MySQL database.

---

## 🏗️ Architecture

```
sprint_commit_2/
├── frontend/           # JavaFX Client Application
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/example/ideaboard/
│   │       │       ├── MainApp.java
│   │       │       └── controllers/
│   │       │           └── CreateIdeaController.java
│   │       └── resources/
│   │           ├── fxml/
│   │           │   └── create_idea.fxml
│   │           └── css/
│   │               └── app.css
│   └── pom.xml
│
└── backend/            # Spring Boot REST API
    ├── src/
    │   └── main/
    │       ├── java/
    │       │   └── com/example/ideaboard/
    │       │       ├── IdeaBoardBackendApplication.java
    │       │       ├── controller/
    │       │       │   └── IdeaController.java
    │       │       ├── service/
    │       │       │   └── IdeaService.java
    │       │       ├── repository/
    │       │       │   └── IdeaRepository.java
    │       │       ├── model/
    │       │       │   └── Idea.java
    │       │       └── config/
    │       │           └── CorsConfig.java
    │       └── resources/
    │           └── application.properties
    └── pom.xml
```

---

## 🚀 Technology Stack

### Frontend
- **JavaFX 21** - Modern UI framework with FXML
- **Java 21+** - Target Java version
- **Maven** - Build and dependency management
- **Java HTTP Client** - REST API communication

### Backend
- **Spring Boot 3.2.0** - Application framework
- **Spring Data JPA** - ORM and database access
- **Hibernate 6.3.1** - JPA implementation
- **Spring Web** - REST API endpoints
- **MySQL 8.0** - Persistent database storage
- **HikariCP** - JDBC connection pooling

---

## 📋 Prerequisites

Before running this application, ensure you have:

1. **Java 21 or higher** (tested with Java 25)
   - Download: [Eclipse Adoptium](https://adoptium.net/)
   - Set `JAVA_HOME` environment variable

2. **Maven 3.9+**
   - Download: [Apache Maven](https://maven.apache.org/download.cgi)
   - Add to system PATH

3. **MySQL 8.0 Server**
   - Download: [MySQL Community Server](https://dev.mysql.com/downloads/mysql/)
   - Install and remember your root password

---

## ⚙️ Database Setup

### 1. Configure MySQL Connection

Edit `backend/src/main/resources/application.properties`:

```properties
# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/ideaboard_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD_HERE
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

**Important:** Replace `YOUR_MYSQL_PASSWORD_HERE` with your actual MySQL root password.

### 2. Database Schema

The `ideas` table will be created automatically by Hibernate with the following structure:

```sql
CREATE TABLE ideas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(255),
    description TEXT,
    status VARCHAR(50),
    owner_name VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

---

## 🏃 Running the Application

### Step 1: Start the Backend (Spring Boot)

Open a terminal in the **backend** folder:

```powershell
cd sprint_commit_2\backend
mvn spring-boot:run
```

Wait for the message:
```
Started IdeaBoardBackendApplication in X.XXX seconds
========================================
IdeaBoard Backend API is running!
API Base URL: http://localhost:8080/api
========================================
```

### Step 2: Start the Frontend (JavaFX)

Open another terminal in the **frontend** folder:

```powershell
cd sprint_commit_2\frontend
mvn javafx:run
```

The JavaFX window will open with the IdeaBoard interface.

---

## 🧪 Testing the Application

### Using the JavaFX Interface

1. Click **"Open Create Idea Form"** button
2. Fill in the form:
   - **Title**: Enter idea title
   - **Category**: Select from dropdown (e.g., "Product Enhancement")
   - **Description**: Enter detailed description
   - **Status**: Select status (e.g., "New", "In Progress")
   - **Owner Name**: Enter your name
3. Click **"Create Idea"**
4. Success message will appear

### Verify Data in MySQL

```sql
mysql -u root -p
USE ideaboard_db;
SELECT * FROM ideas;
```

### Test REST API Endpoints

**Get all ideas:**
```bash
curl http://localhost:8080/api/ideas
```

**Create new idea:**
```bash
curl -X POST http://localhost:8080/api/ideas \
  -H "Content-Type: application/json" \
  -d '{
    "title": "New Feature",
    "category": "Product Enhancement",
    "description": "Add dark mode",
    "status": "New",
    "ownerName": "John Doe"
  }'
```

**Get idea by ID:**
```bash
curl http://localhost:8080/api/ideas/1
```

**Update idea:**
```bash
curl -X PUT http://localhost:8080/api/ideas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Feature",
    "status": "In Progress"
  }'
```

**Delete idea:**
```bash
curl -X DELETE http://localhost:8080/api/ideas/1
```

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/ideas` | Get all ideas |
| GET | `/api/ideas/{id}` | Get idea by ID |
| POST | `/api/ideas` | Create new idea |
| PUT | `/api/ideas/{id}` | Update existing idea |
| DELETE | `/api/ideas/{id}` | Delete idea |
| GET | `/api/ideas/category/{category}` | Get ideas by category |
| GET | `/api/ideas/status/{status}` | Get ideas by status |
| GET | `/api/ideas/owner/{ownerName}` | Get ideas by owner |

---

## 🐛 Troubleshooting

### Backend won't start - Port 8080 already in use

```powershell
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID with actual process ID)
taskkill /PID <PID> /F
```

### MySQL Connection Failed

1. Verify MySQL service is running:
   ```powershell
   Get-Service MySQL80
   ```

2. Test MySQL connection:
   ```powershell
   mysql -u root -p
   ```

3. Check password in `application.properties` matches MySQL root password

### JavaFX window doesn't appear

1. Ensure `JAVA_HOME` is set correctly:
   ```powershell
   echo $env:JAVA_HOME
   ```

2. Verify JavaFX modules are installed:
   ```powershell
   mvn dependency:tree | findstr javafx
   ```

---

## 📦 Building for Production

### Build Backend JAR

```powershell
cd backend
mvn clean package
java -jar target/ideaboard-backend-1.0-SNAPSHOT.jar
```

### Build Frontend JAR

```powershell
cd frontend
mvn clean package
```

---

## 🔧 Configuration

### Change Server Port

Edit `backend/src/main/resources/application.properties`:
```properties
server.port=8081
```

### Enable CORS for Different Origins

Edit `backend/src/main/java/com/example/ideaboard/config/CorsConfig.java`:
```java
.allowedOrigins("http://localhost:3000", "http://example.com")
```

---

## 📝 Features

✅ Create new ideas with title, category, description, status, and owner  
✅ View all ideas from MySQL database  
✅ RESTful API with 8 endpoints  
✅ Persistent storage in MySQL  
✅ JavaFX modern UI with FXML  
✅ Real-time data synchronization  
✅ Cross-origin resource sharing (CORS) enabled  
✅ Connection pooling with HikariCP  
✅ Automatic database schema creation  

---

## 📚 Project Structure Details

### Frontend Key Files

- `MainApp.java` - JavaFX application entry point
- `CreateIdeaController.java` - Form controller with API integration
- `create_idea.fxml` - UI layout definition
- `app.css` - Custom styling
- `module-info.java` - Java module configuration

### Backend Key Files

- `IdeaBoardBackendApplication.java` - Spring Boot main class
- `IdeaController.java` - REST API endpoints
- `IdeaService.java` - Business logic layer
- `IdeaRepository.java` - JPA data access
- `Idea.java` - Entity model with JPA annotations
- `CorsConfig.java` - CORS security configuration

---

## 🤝 Contributing

This is a sprint commit for the Agile Requirements Engineering Tool project.

---

## 📄 License

This project is part of the coursework for Agile Requirements Engineering.

---

## 👥 Author

**kperam1**  
Branch: `uc-1`  
Sprint: 2  
Date: November 6, 2025

---

## 🔗 Repository

GitHub: [Agile-Requirements-Engineering-Tool](https://github.com/kperam1/Agile-Requirements-Engineering-Tool)

---

**Note:** This application requires MySQL 8.0+ to be running. Make sure to configure your MySQL password in `application.properties` before starting the backend.
