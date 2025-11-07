# 🗺️ IdeaBoard Visual Project Map

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                             │
│                        🎯 IdeaBoard Full-Stack Project                      │
│                                                                             │
│           Java 21 + JavaFX 21 + Spring Boot 3.2 + MySQL 8.0+               │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────────┐
│  📖 START HERE - Documentation Hub                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1️⃣  START_HERE.md          ⭐ Main overview - Read this first!            │
│  2️⃣  QUICKSTART.md           🚀 Fast setup guide (3 steps)                 │
│  3️⃣  PROJECT_OVERVIEW.md     📊 Detailed documentation                     │
│  4️⃣  README_FULLSTACK.md     🏗️  Architecture deep-dive                    │
│  5️⃣  ARCHITECTURE.md          🔄 Data flow diagrams                         │
│  6️⃣  FILE_GUIDE.md            📋 This project map                          │
│  7️⃣  README.md                📝 Original JavaFX guide                     │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────────┐
│  🎨 Frontend Layer (JavaFX 21 Desktop UI)                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  📂 src/main/java/                                                          │
│     │                                                                       │
│     ├── module-info.java                   Java 21 module config           │
│     │                                                                       │
│     └── com/example/ideaboard/                                             │
│         │                                                                   │
│         ├── MainApp.java                   🚪 Application entry point      │
│         │   • extends Application                                          │
│         │   • Creates demo window with "Open Create Idea Form" button      │
│         │   • Shows modal dialog integration                               │
│         │                                                                   │
│         ├── controllers/                                                    │
│         │   └── CreateIdeaController.java  🎛️  Form logic & API calls      │
│         │       • @FXML fields (titleField, categoryChoice, etc.)          │
│         │       • initialize() - Set defaults                              │
│         │       • createIdea() - POST to backend ⚡ KEY METHOD             │
│         │       • Validation, JSON building, HTTP request                  │
│         │       • Success/error handling                                   │
│         │                                                                   │
│         └── util/                                                           │
│             └── DialogHelper.java          🛠️  Utility functions           │
│                 • openCreateIdeaDialog() - Reusable modal launcher         │
│                                                                             │
│  📂 src/main/resources/com/example/ideaboard/                               │
│     │                                                                       │
│     ├── views/                                                              │
│     │   └── create_idea.fxml             🖼️  UI Layout (Scene Builder)     │
│     │       • VBox root (modal-surface)                                    │
│     │       • Header: Title + Subtitle                                     │
│     │       • GridPane: 5 form fields                                      │
│     │       • HBox: Cancel + Create Idea buttons                           │
│     │       • Binds to CreateIdeaController                                │
│     │                                                                       │
│     └── styles/                                                             │
│         └── app.css                       🎨 Custom Styles                 │
│             • Modal card (white, rounded, shadow)                          │
│             • Typography (h1, subtitle, labels)                            │
│             • Input styles (TextField, TextArea, ChoiceBox)                │
│             • Focus states (blue ring + glow)                              │
│             • Buttons (primary blue, outline)                              │
│                                                                             │
│  📄 pom.xml                                Maven build config               │
│     • JavaFX dependencies (controls, fxml)                                 │
│     • javafx-maven-plugin                                                  │
│     • Run: mvn javafx:run                                                  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    │  HTTP POST
                                    │  http://localhost:8080/api/ideas
                                    │  Content-Type: application/json
                                    │
                                    ▼

┌─────────────────────────────────────────────────────────────────────────────┐
│  ⚙️ Backend Layer (Spring Boot 3.2 REST API)                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  📂 backend/src/main/java/com/example/ideaboard/                            │
│     │                                                                       │
│     ├── IdeaBoardBackendApplication.java   🚀 Spring Boot Main             │
│     │   • @SpringBootApplication                                           │
│     │   • Starts embedded Tomcat (port 8080)                               │
│     │   • Prints API URL on startup                                        │
│     │                                                                       │
│     ├── model/                                                              │
│     │   └── Idea.java                     💾 JPA Entity (Database Model)   │
│     │       • @Entity, @Table(name="ideas")                                │
│     │       • Fields: id, title, category, description, status,            │
│     │         ownerName, createdAt, updatedAt                              │
│     │       • Validation: @NotBlank, @Size                                 │
│     │       • Lifecycle: @PrePersist, @PreUpdate                           │
│     │                                                                       │
│     ├── repository/                                                         │
│     │   └── IdeaRepository.java           🔍 Data Access Layer             │
│     │       • extends JpaRepository<Idea, Long>                            │
│     │       • Custom queries: findByCategory, findByStatus, search         │
│     │       • Spring generates implementation automatically!               │
│     │                                                                       │
│     ├── service/                                                            │
│     │   └── IdeaService.java              🧠 Business Logic Layer          │
│     │       • @Service                                                     │
│     │       • CRUD methods: create, read, update, delete                  │
│     │       • Filter methods: by category, status                          │
│     │       • Search method                                                │
│     │       • Delegates to repository                                      │
│     │                                                                       │
│     ├── controller/                                                         │
│     │   └── IdeaController.java           🌐 REST API Endpoints            │
│     │       • @RestController, @RequestMapping("/api/ideas")               │
│     │       • POST   /              Create (201 CREATED) ⚡ JavaFX uses   │
│     │       • GET    /              Get all ideas                          │
│     │       • GET    /{id}          Get by ID                              │
│     │       • PUT    /{id}          Update                                 │
│     │       • DELETE /{id}          Delete                                 │
│     │       • GET    /category/{c}  Filter by category                     │
│     │       • GET    /status/{s}    Filter by status                       │
│     │       • GET    /search?q=x    Search by title                        │
│     │                                                                       │
│     └── config/                                                             │
│         └── CorsConfig.java             🔐 CORS Configuration              │
│             • @Configuration                                               │
│             • Allows JavaFX client to call API                             │
│             • Permits all origins/methods (dev mode)                       │
│                                                                             │
│  📂 backend/src/main/resources/                                             │
│     └── application.properties          ⚙️  Spring Boot Config            │
│         • Server port: 8080                                                │
│         • MySQL connection (URL, user, password)                           │
│         • JPA settings (show SQL, auto-update schema)                      │
│         • Logging levels                                                   │
│                                                                             │
│  📄 backend/pom.xml                      Maven build config                 │
│     • Parent: spring-boot-starter-parent                                   │
│     • Dependencies:                                                         │
│       - spring-boot-starter-web (REST API)                                 │
│       - spring-boot-starter-data-jpa (Database ORM)                        │
│       - spring-boot-starter-validation (Input validation)                  │
│       - mysql-connector-j (MySQL driver)                                   │
│       - spring-boot-devtools (auto-reload)                                 │
│     • Run: mvn spring-boot:run                                             │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    │  JDBC
                                    │  JPA/Hibernate ORM
                                    │  SQL: INSERT, SELECT, UPDATE, DELETE
                                    │
                                    ▼

┌─────────────────────────────────────────────────────────────────────────────┐
│  🗄️ Database Layer (MySQL 8.0+)                                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  📂 database/                                                               │
│     └── schema.sql                      🏗️  Database Setup Script          │
│         • CREATE DATABASE ideaboard_db                                     │
│         • CREATE TABLE ideas (8 columns)                                   │
│         • Indexes: category, status, owner_name, created_at                │
│         • Sample data: 5 ideas                                             │
│         • UTF-8 encoding                                                   │
│                                                                             │
│  💾 Database: ideaboard_db                                                  │
│  📊 Table: ideas                                                            │
│                                                                             │
│  ┌─────┬──────────┬───────────┬─────────┬────────┬────────────┬──────────┐│
│  │ id  │  title   │ category  │ descrip │ status │ owner_name │ created  ││
│  ├─────┼──────────┼───────────┼─────────┼────────┼────────────┼──────────┤│
│  │ 1   │ Dark Mode│ Product...│ Add...  │ New    │ John Doe   │ 2025-... ││
│  │ 2   │ Mobile...│ New Feat..│ Develop │ Review │ Jane Smith │ 2025-... ││
│  │ ... │ ...      │ ...       │ ...     │ ...    │ ...        │ ...      ││
│  └─────┴──────────┴───────────┴─────────┴────────┴────────────┴──────────┘│
│                                                                             │
│  • Auto-increment ID                                                        │
│  • Timestamps (created_at, updated_at) auto-managed                        │
│  • Persistent storage                                                       │
│  • Viewable in MySQL Workbench                                             │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────────┐
│  🛠️ Development Tools & Scripts                                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  📄 launch.bat                          🚀 One-Click App Launcher           │
│     • Checks MySQL connection                                              │
│     • Starts backend in separate window                                    │
│     • Starts frontend in current window                                    │
│     • Usage: .\launch.bat                                                  │
│                                                                             │
│  📄 setup-database.bat                  🏗️  Database Setup Helper          │
│     • Interactive database initialization                                  │
│     • Runs schema.sql                                                      │
│     • Error handling                                                       │
│     • Usage: .\setup-database.bat                                          │
│                                                                             │
│  📄 test-api.http                       🧪 API Testing (REST Client)        │
│     • Sample requests for all 8 endpoints                                  │
│     • GET, POST, PUT, DELETE examples                                      │
│     • JSON payloads included                                               │
│     • Usage: Open in VS Code, click "Send Request"                         │
│                                                                             │
│  📄 ideaboard.code-workspace            💼 VS Code Multi-Root Workspace     │
│     • Folders: Frontend (root), Backend (backend/)                         │
│     • Java settings configured                                             │
│     • Recommends extensions (Java, Spring, MySQL, REST Client)             │
│     • Usage: code ideaboard.code-workspace                                 │
│                                                                             │
│  📄 .gitignore                          🚫 Git Exclusions                   │
│     • Ignores: target/, .idea/, .vscode/, *.log                            │
│     • Ready for GitHub push                                                │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────────┐
│  🔄 Data Flow Visualization                                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  User fills form in JavaFX                                                 │
│         │                                                                   │
│         │ 1. Validation (title required)                                   │
│         ▼                                                                   │
│  CreateIdeaController.createIdea()                                         │
│         │                                                                   │
│         │ 2. Build JSON payload                                            │
│         │ 3. HTTP POST request                                             │
│         ▼                                                                   │
│  http://localhost:8080/api/ideas                                           │
│         │                                                                   │
│         │ 4. Spring Boot receives                                          │
│         ▼                                                                   │
│  IdeaController.createIdea(@RequestBody Idea)                              │
│         │                                                                   │
│         │ 5. Validates with @Valid                                         │
│         ▼                                                                   │
│  IdeaService.createIdea(idea)                                              │
│         │                                                                   │
│         │ 6. Business logic                                                │
│         ▼                                                                   │
│  IdeaRepository.save(idea)                                                 │
│         │                                                                   │
│         │ 7. JPA/Hibernate converts to SQL                                 │
│         ▼                                                                   │
│  INSERT INTO ideas (...) VALUES (...)                                      │
│         │                                                                   │
│         │ 8. MySQL stores data                                             │
│         ▼                                                                   │
│  Row inserted, ID generated                                                │
│         │                                                                   │
│         │ 9. Response flows back up                                        │
│         ▼                                                                   │
│  HTTP 201 CREATED + JSON body                                              │
│         │                                                                   │
│         │ 10. JavaFX receives response                                     │
│         ▼                                                                   │
│  Show success alert, clear form, close modal                               │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────────┐
│  📊 Project Statistics                                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Total Files:              32+                                             │
│  Lines of Code:            ~1,800                                          │
│  Java Classes:             10 (3 frontend, 7 backend)                      │
│  FXML Files:               1                                               │
│  CSS Files:                1                                               │
│  SQL Scripts:              1                                               │
│  Config Files:             6                                               │
│  Scripts:                  2                                               │
│  Documentation:            7 markdown files                                │
│                                                                             │
│  API Endpoints:            8                                               │
│  Database Tables:          1                                               │
│  UI Forms:                 1 (Create Idea modal)                           │
│                                                                             │
│  Technologies:             Java 21, JavaFX 21, Spring Boot 3.2, MySQL 8.0+ │
│  Build Tool:               Maven                                           │
│  Architecture:             3-tier (Presentation, API, Data)                │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────────┐
│  ✅ Implemented Features                                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Frontend (JavaFX):                                                         │
│   ✓ Create New Idea modal form                                            │
│   ✓ Modern UI (rounded, shadowed, blue focus)                             │
│   ✓ Input validation (title required)                                     │
│   ✓ Default values (Category, Status)                                     │
│   ✓ Success/error alerts                                                  │
│   ✓ HTTP POST integration with backend                                    │
│   ✓ Keyboard shortcuts (Enter, Escape)                                    │
│                                                                             │
│  Backend (Spring Boot):                                                     │
│   ✓ Complete REST API (8 endpoints)                                       │
│   ✓ CRUD operations (Create, Read, Update, Delete)                        │
│   ✓ Filter & search endpoints                                             │
│   ✓ JPA/Hibernate ORM                                                      │
│   ✓ MySQL integration                                                      │
│   ✓ Input validation (@Valid, @NotBlank)                                  │
│   ✓ CORS enabled for JavaFX                                               │
│   ✓ Auto-reload (Spring DevTools)                                         │
│                                                                             │
│  Database (MySQL):                                                          │
│   ✓ Schema created (ideaboard_db)                                         │
│   ✓ Ideas table with indexes                                              │
│   ✓ Auto-timestamps                                                        │
│   ✓ Sample data (5 ideas)                                                 │
│   ✓ UTF-8 encoding                                                         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────────┐
│  🎯 Quick Commands                                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Setup database (one-time):                                                │
│    .\setup-database.bat                                                    │
│                                                                             │
│  Launch entire app:                                                        │
│    .\launch.bat                                                            │
│                                                                             │
│  Start backend only:                                                       │
│    cd backend                                                              │
│    mvn spring-boot:run                                                     │
│                                                                             │
│  Start frontend only:                                                      │
│    mvn clean javafx:run                                                    │
│                                                                             │
│  Test API:                                                                 │
│    Open test-api.http in VS Code                                          │
│    Click "Send Request" above each endpoint                                │
│                                                                             │
│  Open workspace:                                                           │
│    code ideaboard.code-workspace                                           │
│                                                                             │
│  View database:                                                            │
│    Open MySQL Workbench → ideaboard_db → ideas table                      │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────────┐
│  🚀 Next Steps Roadmap                                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Phase 1: Core CRUD UI ⭐ NEXT                                             │
│   □ Ideas List View (TableView)                                           │
│   □ Edit Idea functionality                                               │
│   □ Delete Idea with confirmation                                         │
│   □ Refresh data button                                                   │
│                                                                             │
│  Phase 2: Search & Filter                                                  │
│   □ Search box (real-time)                                                │
│   □ Category filter dropdown                                              │
│   □ Status filter dropdown                                                │
│   □ Sort by date/title                                                    │
│                                                                             │
│  Phase 3: Polish                                                            │
│   □ Loading indicators                                                    │
│   □ Pagination                                                            │
│   □ Keyboard shortcuts                                                    │
│   □ Better error handling                                                 │
│                                                                             │
│  Phase 4: Advanced                                                          │
│   □ User authentication                                                   │
│   □ Comments system                                                       │
│   □ File attachments                                                      │
│   □ Export to CSV/PDF                                                     │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────────┐
│  📚 Where to Find Information                                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Getting started?          → START_HERE.md                                 │
│  Quick setup?              → QUICKSTART.md                                 │
│  Detailed docs?            → PROJECT_OVERVIEW.md                           │
│  Architecture info?        → ARCHITECTURE.md, README_FULLSTACK.md          │
│  File descriptions?        → FILE_GUIDE.md                                 │
│  Visual overview?          → This file (PROJECT_MAP.md)                    │
│                                                                             │
│  Troubleshooting?          → START_HERE.md (Troubleshooting section)       │
│  API testing?              → test-api.http                                 │
│  Database schema?          → database/schema.sql                           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘


═══════════════════════════════════════════════════════════════════════════════

                     🎉 IdeaBoard - Ready for Development! 🎉

                  Full-Stack Java Application with Modern Architecture
                     
                   JavaFX Desktop ↔ Spring Boot API ↔ MySQL Database

═══════════════════════════════════════════════════════════════════════════════
```
