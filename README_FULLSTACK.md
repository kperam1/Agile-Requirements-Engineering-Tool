# IdeaBoard - Full Stack Application

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                     JavaFX Frontend                         │
│  (Java 21 + JavaFX 21 + FXML + Scene Builder)              │
│                                                             │
│  • Create/Edit/Delete Ideas                                │
│  • Modern UI with rounded corners, shadows, focus states   │
│  • REST client using Java 21 HttpClient                    │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      │ HTTP/JSON
                      │ (REST API)
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                  Spring Boot Backend                        │
│            (Java 21 + Spring Boot 3.2)                      │
│                                                             │
│  • RESTful API endpoints                                   │
│  • Business logic & validation                             │
│  • JPA/Hibernate ORM                                       │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      │ JDBC
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                   MySQL Database                            │
│                                                             │
│  • ideas table (id, title, category, description, etc.)    │
│  • Persistent data storage                                 │
└─────────────────────────────────────────────────────────────┘
```

## 📁 Project Structure

```
idea/
├── frontend (JavaFX)
│   ├── src/main/
│   │   ├── java/com/example/ideaboard/
│   │   │   ├── MainApp.java
│   │   │   ├── controllers/
│   │   │   │   └── CreateIdeaController.java
│   │   │   └── util/
│   │   │       └── DialogHelper.java
│   │   └── resources/com/example/ideaboard/
│   │       ├── views/
│   │       │   └── create_idea.fxml
│   │       └── styles/
│   │           └── app.css
│   └── pom.xml
│
├── backend/ (Spring Boot)
│   ├── src/main/
│   │   ├── java/com/example/ideaboard/
│   │   │   ├── IdeaBoardBackendApplication.java
│   │   │   ├── model/
│   │   │   │   └── Idea.java
│   │   │   ├── repository/
│   │   │   │   └── IdeaRepository.java
│   │   │   ├── service/
│   │   │   │   └── IdeaService.java
│   │   │   ├── controller/
│   │   │   │   └── IdeaController.java
│   │   │   └── config/
│   │   │       └── CorsConfig.java
│   │   └── resources/
│   │       └── application.properties
│   └── pom.xml
│
└── database/
    └── schema.sql
```

## 🚀 Quick Start Guide

### Prerequisites

- **Java 21** (JDK 21)
- **Maven 3.8+**
- **MySQL 8.0+** (or MySQL Workbench)
- **VS Code** (with Java extensions)

### Step 1: Setup MySQL Database

1. **Start MySQL Server** (via MySQL Workbench or command line)

2. **Create Database** - Run the SQL script:
   ```sql
   -- In MySQL Workbench or command line:
   source database/schema.sql
   ```
   
   Or manually:
   ```sql
   CREATE DATABASE ideaboard_db;
   USE ideaboard_db;
   -- Then run the schema.sql content
   ```

3. **Update Database Credentials** (if needed):
   
   Edit `backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

### Step 2: Start Backend Server

```powershell
cd backend
mvn clean install
mvn spring-boot:run
```

**Expected output:**
```
========================================
IdeaBoard Backend API is running!
API Base URL: http://localhost:8080/api
========================================
```

**Test API:** Open browser to `http://localhost:8080/api/ideas`

### Step 3: Run JavaFX Frontend

Open a **new terminal** (keep backend running):

```powershell
cd ..  # Back to root 'idea' folder
mvn clean javafx:run
```

Click **"Open Create Idea Form"** button to test the modal!

## 🔌 REST API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/ideas` | Get all ideas |
| `GET` | `/api/ideas/{id}` | Get idea by ID |
| `POST` | `/api/ideas` | Create new idea |
| `PUT` | `/api/ideas/{id}` | Update idea |
| `DELETE` | `/api/ideas/{id}` | Delete idea |
| `GET` | `/api/ideas/category/{category}` | Get ideas by category |
| `GET` | `/api/ideas/status/{status}` | Get ideas by status |
| `GET` | `/api/ideas/search?keyword={text}` | Search ideas by title |

### Example API Calls

**Create Idea:**
```bash
curl -X POST http://localhost:8080/api/ideas \
  -H "Content-Type: application/json" \
  -d '{
    "title": "AI-Powered Search",
    "category": "New Feature",
    "description": "Implement machine learning for smarter search",
    "status": "New",
    "ownerName": "Alice Johnson"
  }'
```

**Get All Ideas:**
```bash
curl http://localhost:8080/api/ideas
```

## 🗄️ Database Schema

**Table: `ideas`**

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| `title` | VARCHAR(200) | NOT NULL |
| `category` | VARCHAR(50) | NOT NULL |
| `description` | TEXT | NULL |
| `status` | VARCHAR(50) | NOT NULL |
| `owner_name` | VARCHAR(100) | NULL |
| `created_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| `updated_at` | DATETIME | NOT NULL, AUTO UPDATE |

## 🔧 Configuration Files

### Backend: `application.properties`

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/ideaboard_db
spring.datasource.username=root
spring.datasource.password=

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Frontend: JavaFX integrates with backend via HttpClient

The `CreateIdeaController.java` now sends POST requests to:
```
http://localhost:8080/api/ideas
```

## 🧪 Testing

### Test Backend API
```powershell
# Windows PowerShell
Invoke-WebRequest -Uri http://localhost:8080/api/ideas -Method GET
```

### Test Database Connection
```sql
-- In MySQL Workbench
USE ideaboard_db;
SELECT * FROM ideas;
```

## 🛠️ Development Tools

### VS Code Extensions (Recommended)
- **Extension Pack for Java** (Microsoft)
- **Spring Boot Extension Pack** (VMware)
- **MySQL** (cweijan)
- **REST Client** (Huachao Mao)

### MySQL Workbench
- Database design and modeling
- Query execution
- Data import/export

### Git & GitHub
- Version control
- Collaboration
- CI/CD pipelines

### Taiga
- Project management
- Issue tracking
- Sprint planning

## 📊 Sample Data

The `schema.sql` includes 5 sample ideas:
1. Implement Dark Mode
2. Mobile App Development
3. Performance Optimization
4. User Analytics Dashboard
5. API Rate Limiting

## 🚨 Troubleshooting

### Backend won't start
- **Check MySQL is running**: `SHOW DATABASES;`
- **Verify credentials** in `application.properties`
- **Check port 8080** is not in use: `netstat -an | findstr :8080`

### Frontend can't connect
- **Ensure backend is running**: Visit `http://localhost:8080/api/ideas`
- **Check CORS configuration** in `CorsConfig.java`
- **Look for connection errors** in JavaFX console

### Database issues
- **Run schema.sql** to recreate tables
- **Check Hibernate logs** in Spring Boot console
- **Verify MySQL service** is active

## 📈 Next Steps

1. **Add Authentication** - Spring Security + JWT
2. **Create List View** - Display all ideas in JavaFX TableView
3. **Add Edit/Delete** - Complete CRUD operations
4. **Implement Search** - Filter and search functionality
5. **Add Pagination** - Handle large datasets
6. **Deploy** - Docker containers + cloud hosting

## 📝 License

MIT License - See LICENSE file for details

---

**Built with:**
- Java 21
- JavaFX 21
- Spring Boot 3.2
- MySQL 8.0
- Maven

**Happy Coding! 🚀**
