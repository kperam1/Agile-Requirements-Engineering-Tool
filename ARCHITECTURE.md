# 🔄 IdeaBoard Data Flow Architecture

## Complete Request Flow: Create New Idea

```
┌─────────────────────────────────────────────────────────────────┐
│                          USER INTERACTION                        │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 │ 1. Clicks "Create Idea"
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                   JAVAFX FRONTEND LAYER                          │
│  ┌────────────────────────────────────────────────────────┐    │
│  │ create_idea.fxml (UI)                                  │    │
│  │  • Title input field                                    │    │
│  │  • Category dropdown (default: Product Enhancement)     │    │
│  │  • Description text area                                │    │
│  │  • Status dropdown (default: New)                       │    │
│  │  • Owner name field                                     │    │
│  │  • [Cancel] [Create Idea] buttons                       │    │
│  └─────────────────────┬──────────────────────────────────┘    │
│                        │ 2. User fills form & clicks Create    │
│                        ▼                                        │
│  ┌────────────────────────────────────────────────────────┐    │
│  │ CreateIdeaController.java                              │    │
│  │  @FXML createIdea() method:                            │    │
│  │    1. Validates title (required)                        │    │
│  │    2. Gathers form data                                 │    │
│  │    3. Escapes JSON special chars                        │    │
│  │    4. Builds JSON payload                               │    │
│  └─────────────────────┬──────────────────────────────────┘    │
└─────────────────────────┼──────────────────────────────────────┘
                          │
                          │ 3. HTTP POST Request
                          │    URL: http://localhost:8080/api/ideas
                          │    Method: POST
                          │    Headers: Content-Type: application/json
                          │    Body: {
                          │      "title": "...",
                          │      "category": "...",
                          │      "description": "...",
                          │      "status": "...",
                          │      "ownerName": "..."
                          │    }
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                   SPRING BOOT BACKEND LAYER                      │
│  ┌────────────────────────────────────────────────────────┐    │
│  │ IdeaController.java (REST Controller)                  │    │
│  │  @PostMapping("/api/ideas")                            │    │
│  │  createIdea(@Valid @RequestBody Idea idea)            │    │
│  │    • Receives JSON request                              │    │
│  │    • Validates with @Valid annotation                   │    │
│  │    • Returns 201 CREATED on success                     │    │
│  └─────────────────────┬──────────────────────────────────┘    │
│                        │ 4. Passes Idea object to service      │
│                        ▼                                        │
│  ┌────────────────────────────────────────────────────────┐    │
│  │ IdeaService.java (Business Logic)                      │    │
│  │  createIdea(Idea idea)                                 │    │
│  │    • Business validation (if any)                       │    │
│  │    • Calls repository to persist                        │    │
│  └─────────────────────┬──────────────────────────────────┘    │
│                        │ 5. Delegates to repository            │
│                        ▼                                        │
│  ┌────────────────────────────────────────────────────────┐    │
│  │ IdeaRepository.java (Data Access)                      │    │
│  │  extends JpaRepository<Idea, Long>                     │    │
│  │    • save(idea) - JPA magic!                            │    │
│  └─────────────────────┬──────────────────────────────────┘    │
│                        │ 6. JPA/Hibernate ORM translation      │
│                        ▼                                        │
│  ┌────────────────────────────────────────────────────────┐    │
│  │ Idea.java (JPA Entity)                                 │    │
│  │  @Entity, @Table(name="ideas")                         │    │
│  │  • Maps Java object to database table                   │    │
│  │  • @PrePersist sets createdAt timestamp                │    │
│  └─────────────────────┬──────────────────────────────────┘    │
└─────────────────────────┼──────────────────────────────────────┘
                          │
                          │ 7. SQL INSERT statement
                          │    INSERT INTO ideas (title, category, 
                          │    description, status, owner_name, 
                          │    created_at, updated_at)
                          │    VALUES (?, ?, ?, ?, ?, NOW(), NOW());
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                      MYSQL DATABASE LAYER                        │
│  ┌────────────────────────────────────────────────────────┐    │
│  │ Database: ideaboard_db                                 │    │
│  │ Table: ideas                                           │    │
│  │                                                         │    │
│  │ ┌───┬──────────┬────────────┬────────┬────────────┐  │    │
│  │ │id │ title    │ category   │ status │ created_at │  │    │
│  │ ├───┼──────────┼────────────┼────────┼────────────┤  │    │
│  │ │ 6 │Dark Mode │Product Enh │ New    │2025-11-05  │  │    │
│  │ └───┴──────────┴────────────┴────────┴────────────┘  │    │
│  │                                                         │    │
│  │ • Auto-increment ID generated                           │    │
│  │ • Timestamps set automatically                          │    │
│  │ • Data persisted to disk                                │    │
│  └─────────────────────┬──────────────────────────────────┘    │
└─────────────────────────┼──────────────────────────────────────┘
                          │
                          │ 8. Success confirmation
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                        RESPONSE FLOW                             │
└─────────────────────────────────────────────────────────────────┘
                          │
                          │ 9. Database returns inserted row
                          │    with generated ID
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                      SPRING BOOT BACKEND                         │
│                                                                  │
│  Repository → Service → Controller                              │
│                                                                  │
│  Returns: HTTP 201 CREATED                                      │
│  Body: {                                                        │
│    "id": 6,                                                     │
│    "title": "Dark Mode",                                        │
│    "category": "Product Enhancement",                           │
│    "description": "...",                                        │
│    "status": "New",                                             │
│    "ownerName": "...",                                          │
│    "createdAt": "2025-11-05T10:30:00",                         │
│    "updatedAt": "2025-11-05T10:30:00"                          │
│  }                                                              │
└─────────────────────────┬───────────────────────────────────────┘
                          │
                          │ 10. HTTP Response with JSON
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                      JAVAFX FRONTEND                             │
│                                                                  │
│  CreateIdeaController receives response:                        │
│    • Checks response.statusCode()                               │
│    • If 200/201: showSuccess() alert                           │
│    • If error: showError() alert                               │
│    • clearFields() - reset form                                │
│    • closeWindow() - dismiss modal                             │
└─────────────────────────┬───────────────────────────────────────┘
                          │
                          │ 11. User sees success message
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                           USER SEES                              │
│                                                                  │
│  ┌──────────────────────────────────────────────────┐          │
│  │  ℹ️  Success                                      │          │
│  │                                                   │          │
│  │  Idea 'Dark Mode' created successfully!          │          │
│  │                                                   │          │
│  │                            [ OK ]                 │          │
│  └──────────────────────────────────────────────────┘          │
│                                                                  │
│  • Modal closes                                                 │
│  • Data is safely stored in MySQL                              │
│  • Can be retrieved later via GET /api/ideas                   │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔍 Technology Mapping

### Layer 1: Presentation (JavaFX)
```
User Interface → FXML Layout → Controller Logic
```
- **Files:** `create_idea.fxml`, `CreateIdeaController.java`, `app.css`
- **Responsibility:** User interaction, input validation, HTTP requests
- **Technology:** JavaFX 21, Java 21 HttpClient

### Layer 2: API Gateway (Spring Boot REST)
```
HTTP Endpoint → Controller → Service → Repository
```
- **Files:** `IdeaController.java`, `IdeaService.java`, `IdeaRepository.java`
- **Responsibility:** Request handling, business logic, data access
- **Technology:** Spring Boot 3.2, Spring Web, Spring Data JPA

### Layer 3: Data Persistence (MySQL)
```
JPA Entity → Hibernate ORM → SQL → MySQL Table
```
- **Files:** `Idea.java` (entity), `schema.sql` (structure)
- **Responsibility:** Data storage, querying, transactions
- **Technology:** MySQL 8.0, Hibernate, JDBC

---

## 📊 Data Transformation Journey

### 1. User Input (Form)
```
Title: "Implement Dark Mode"
Category: "Product Enhancement" (dropdown)
Description: "Add dark theme..."
Status: "New" (dropdown)
Owner: "John Doe"
```

### 2. JavaFX Controller (Java Object)
```java
String title = "Implement Dark Mode";
String category = "Product Enhancement";
String description = "Add dark theme...";
String status = "New";
String owner = "John Doe";
```

### 3. HTTP Request (JSON)
```json
{
  "title": "Implement Dark Mode",
  "category": "Product Enhancement",
  "description": "Add dark theme...",
  "status": "New",
  "ownerName": "John Doe"
}
```

### 4. Spring Boot (Java Entity)
```java
Idea idea = new Idea();
idea.setTitle("Implement Dark Mode");
idea.setCategory("Product Enhancement");
idea.setDescription("Add dark theme...");
idea.setStatus("New");
idea.setOwnerName("John Doe");
// createdAt/updatedAt set by @PrePersist
```

### 5. Hibernate (SQL)
```sql
INSERT INTO ideas 
(title, category, description, status, owner_name, created_at, updated_at)
VALUES 
('Implement Dark Mode', 'Product Enhancement', 'Add dark theme...', 
 'New', 'John Doe', '2025-11-05 10:30:00', '2025-11-05 10:30:00');
```

### 6. MySQL (Stored Data)
```
+----+---------------------+---------------------+--------+----------+---------------------+---------------------+
| id | title               | category            | status | owner    | created_at          | updated_at          |
+----+---------------------+---------------------+--------+----------+---------------------+---------------------+
| 6  | Implement Dark Mode | Product Enhancement | New    | John Doe | 2025-11-05 10:30:00 | 2025-11-05 10:30:00 |
+----+---------------------+---------------------+--------+----------+---------------------+---------------------+
```

### 7. Response (JSON - sent back to JavaFX)
```json
{
  "id": 6,
  "title": "Implement Dark Mode",
  "category": "Product Enhancement",
  "description": "Add dark theme...",
  "status": "New",
  "ownerName": "John Doe",
  "createdAt": "2025-11-05T10:30:00",
  "updatedAt": "2025-11-05T10:30:00"
}
```

### 8. User Confirmation (Alert)
```
✅ Success
Idea 'Implement Dark Mode' created successfully!
```

---

## 🔐 Security & Validation Flow

```
┌─────────────────────────────────────────────────┐
│ Frontend Validation (CreateIdeaController)      │
│  ✓ Title not empty                              │
│  ✓ JSON escaping                                │
└──────────────┬──────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────┐
│ Backend Validation (Spring Boot)                │
│  ✓ @Valid annotation                            │
│  ✓ @NotBlank on title                           │
│  ✓ @Size max 200 chars                          │
└──────────────┬──────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────┐
│ Database Constraints (MySQL)                    │
│  ✓ NOT NULL on title, category, status          │
│  ✓ VARCHAR length limits                        │
│  ✓ Data type enforcement                        │
└─────────────────────────────────────────────────┘
```

**Triple-layer validation ensures data integrity!**

---

## 🚀 Performance Optimizations

### Current Implementation
- ✅ Connection pooling (HikariCP - Spring Boot default)
- ✅ Database indexes on category, status, owner_name
- ✅ JPA second-level cache ready
- ✅ Lazy loading for relationships (future)

### Future Enhancements
- [ ] Frontend: Async HTTP calls (CompletableFuture)
- [ ] Backend: Caching with Redis
- [ ] Database: Query optimization, pagination
- [ ] API: Rate limiting, compression

---

## 🧩 Component Interactions

```
MainApp.java
    │
    │ opens modal
    ▼
DialogHelper.openCreateIdeaDialog()
    │
    │ loads FXML
    ▼
create_idea.fxml
    │
    │ applies styles
    ├─→ app.css
    │
    │ binds controller
    ▼
CreateIdeaController.java
    │
    │ on "Create Idea" button
    ├─→ validate()
    ├─→ buildJson()
    ├─→ sendHttpRequest()
    │       │
    │       │ HTTP POST
    │       ▼
    │   IdeaController.java (@RestController)
    │       │
    │       │ delegates
    │       ▼
    │   IdeaService.java
    │       │
    │       │ calls
    │       ▼
    │   IdeaRepository.java
    │       │
    │       │ JPA/Hibernate
    │       ▼
    │   MySQL Database
    │
    │   (response flows back up)
    │
    ├─→ showSuccess() or showError()
    └─→ clearFields() + closeWindow()
```

---

## 📡 HTTP Communication Details

### Request Headers
```
POST /api/ideas HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Content-Length: 145
```

### Request Body
```json
{
  "title": "Implement Dark Mode",
  "category": "Product Enhancement",
  "description": "Add dark theme support",
  "status": "New",
  "ownerName": "John Doe"
}
```

### Response (Success)
```
HTTP/1.1 201 Created
Content-Type: application/json
Transfer-Encoding: chunked
Date: Tue, 05 Nov 2025 10:30:00 GMT

{
  "id": 6,
  "title": "Implement Dark Mode",
  ...
}
```

### Response (Error - Validation)
```
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
  "timestamp": "2025-11-05T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Title is required",
  "path": "/api/ideas"
}
```

---

## 🔄 State Management

### Frontend State
- **Form State:** Managed by JavaFX controls (TextField, ChoiceBox)
- **Validation State:** Temporary, in `createIdea()` method
- **No persistent state** (stateless modal)

### Backend State
- **Stateless REST API** (each request independent)
- **Session:** None (future: Spring Security sessions)
- **Database:** Single source of truth

### Database State
- **Persistent:** All data stored permanently
- **Transactions:** ACID compliant
- **Timestamps:** Auto-tracked for audit trail

---

## 🎯 Key Design Decisions

| Decision | Reasoning |
|----------|-----------|
| **Separation of Concerns** | Frontend/Backend/Database clearly separated |
| **RESTful API** | Standard, scalable, tool-friendly |
| **JPA/Hibernate** | Abstract SQL, portable across databases |
| **FXML for UI** | Declarative, Scene Builder compatible |
| **Maven** | Dependency management, build automation |
| **JSON** | Lightweight, human-readable data format |
| **HTTP** | Standard protocol, works everywhere |

---

## 📈 Scalability Path

```
Current:
[JavaFX Desktop] → [Spring Boot] → [MySQL]
     (1 user)         (1 server)    (1 DB)

Future - Medium Scale:
[JavaFX Desktop] → [Load Balancer] → [Spring Boot × 3] → [MySQL Primary]
  (100 users)                             (3 servers)         ↓
                                                        [MySQL Replicas]

Future - Large Scale:
[Web/Mobile/Desktop] → [API Gateway] → [Microservices] → [MySQL Cluster]
    (10k users)           [CDN]            [Kafka]          [Redis Cache]
                          [Auth]           [Search]         [Analytics]
```

---

**This architecture provides a solid foundation for growth! 🚀**
