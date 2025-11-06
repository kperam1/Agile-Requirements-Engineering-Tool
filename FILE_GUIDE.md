# 📋 File Directory & Purpose Guide

Quick reference for every file in the project.

## 📂 Project Root Structure

```
c:\Users\disha\Downloads\idea\
│
├── 📂 src/                              [Frontend JavaFX Source]
├── 📂 backend/                          [Backend Spring Boot Source]
├── 📂 database/                         [SQL Scripts]
├── 📄 pom.xml                           Maven config (Frontend)
├── 📄 .gitignore                        Git exclusions
├── 📄 ideaboard.code-workspace          VS Code workspace
├── 📄 launch.bat                        App launcher script
├── 📄 setup-database.bat                Database setup script
├── 📄 test-api.http                     API testing file
│
└── 📚 Documentation/
    ├── START_HERE.md                    👈 BEGIN HERE! Complete overview
    ├── QUICKSTART.md                    Fast setup guide
    ├── PROJECT_OVERVIEW.md              Detailed project documentation
    ├── README_FULLSTACK.md              Architecture deep-dive
    ├── ARCHITECTURE.md                  Data flow diagrams
    ├── README.md                        Original JavaFX guide
    └── FILE_GUIDE.md                    This file
```

---

## 🎨 Frontend Files (JavaFX)

### Java Source Files

**`src/main/java/module-info.java`**
- Java Platform Module System descriptor
- Declares dependencies: javafx.controls, javafx.fxml, java.net.http
- Exports/opens packages for JavaFX and reflection

**`src/main/java/com/example/ideaboard/MainApp.java`**
- Main application entry point
- Extends `javafx.application.Application`
- Creates demo window with "Open Create Idea Form" button
- Shows how to load CSS and open modal dialogs

**`src/main/java/com/example/ideaboard/controllers/CreateIdeaController.java`**
- Controller for Create Idea modal form
- **KEY FILE:** Contains API integration logic
- Methods:
  - `initialize()` - Sets default dropdown values
  - `createIdea()` - Validates, builds JSON, sends HTTP POST to backend
  - `cancel()` - Clears and closes form
  - `escapeJson()` - Escapes special characters for JSON
- Uses Java 21 HttpClient for REST API calls

**`src/main/java/com/example/ideaboard/util/DialogHelper.java`**
- Utility class for opening dialogs
- `openCreateIdeaDialog()` - Loads FXML, applies CSS, shows modal
- Reusable across application

### FXML Layout Files

**`src/main/resources/com/example/ideaboard/views/create_idea.fxml`**
- Declarative UI layout (XML format)
- Scene Builder compatible
- Components:
  - VBox root container (styleClass="modal-surface")
  - Header labels (title + subtitle)
  - GridPane with 5 form fields
  - HBox with Cancel/Create buttons
- Binds to `CreateIdeaController`

### CSS Style Files

**`src/main/resources/com/example/ideaboard/styles/app.css`**
- Custom JavaFX styling
- Defines:
  - Global font, colors, background
  - `.modal-surface` - Card appearance
  - `.h1`, `.subtitle`, `.field-label` - Typography
  - Input styles (TextField, TextArea, ChoiceBox)
  - Focus states (blue ring + glow)
  - Button styles (primary + outline)
  - Spacing helpers

### Build Configuration

**`pom.xml` (Frontend)**
- Maven Project Object Model
- Dependencies:
  - JavaFX controls (21)
  - JavaFX FXML (21)
- Plugins:
  - maven-compiler-plugin (Java 21)
  - javafx-maven-plugin (run with `mvn javafx:run`)
- Project: com.example.ideaboard (frontend)

---

## ⚙️ Backend Files (Spring Boot)

### Java Source Files

**`backend/src/main/java/com/example/ideaboard/IdeaBoardBackendApplication.java`**
- Spring Boot main application class
- `@SpringBootApplication` annotation (auto-configuration)
- `main()` method starts embedded Tomcat server
- Prints startup message with API URL

**`backend/src/main/java/com/example/ideaboard/model/Idea.java`**
- JPA Entity representing database table
- `@Entity`, `@Table(name="ideas")` annotations
- Fields:
  - id (Long, auto-generated)
  - title, category, description, status, ownerName (Strings)
  - createdAt, updatedAt (LocalDateTime, auto-managed)
- Validation: `@NotBlank`, `@Size`
- Lifecycle: `@PrePersist`, `@PreUpdate` for timestamps

**`backend/src/main/java/com/example/ideaboard/repository/IdeaRepository.java`**
- Spring Data JPA repository interface
- Extends `JpaRepository<Idea, Long>`
- Custom query methods:
  - `findByCategory(String)`
  - `findByStatus(String)`
  - `findByOwnerName(String)`
  - `findByTitleContainingIgnoreCase(String)` - Search
- No implementation needed (Spring generates!)

**`backend/src/main/java/com/example/ideaboard/service/IdeaService.java`**
- Business logic layer
- `@Service` annotation
- Methods:
  - `getAllIdeas()`, `getIdeaById(Long)`
  - `createIdea(Idea)`, `updateIdea(Long, Idea)`
  - `deleteIdea(Long)`
  - `getIdeasByCategory(String)`, `getIdeasByStatus(String)`
  - `searchIdeas(String)`
- Handles exceptions, delegates to repository

**`backend/src/main/java/com/example/ideaboard/controller/IdeaController.java`**
- REST API endpoints
- `@RestController`, `@RequestMapping("/api/ideas")`
- Endpoints:
  - `POST /` - Create idea (used by JavaFX)
  - `GET /` - Get all ideas
  - `GET /{id}` - Get by ID
  - `PUT /{id}` - Update
  - `DELETE /{id}` - Delete
  - `GET /category/{category}` - Filter
  - `GET /status/{status}` - Filter
  - `GET /search?keyword=x` - Search
- Returns `ResponseEntity<T>` with HTTP status codes

**`backend/src/main/java/com/example/ideaboard/config/CorsConfig.java`**
- CORS (Cross-Origin Resource Sharing) configuration
- Allows JavaFX client to call API (different "origin")
- `@Configuration`, `@Bean`
- Permits all origins, methods, headers (for development)

### Configuration Files

**`backend/src/main/resources/application.properties`**
- Spring Boot configuration
- Settings:
  - Server port (8080)
  - MySQL connection URL, username, password
  - JPA settings (show SQL, auto-update schema)
  - Logging levels
  - CORS (additional config)
  - Jackson JSON settings

**`backend/pom.xml`**
- Maven Project Object Model
- Parent: spring-boot-starter-parent (3.2.0)
- Dependencies:
  - spring-boot-starter-web (REST API)
  - spring-boot-starter-data-jpa (Database)
  - spring-boot-starter-validation (Input validation)
  - mysql-connector-j (MySQL driver)
  - lombok (optional, code generation)
  - spring-boot-devtools (auto-reload)
- Plugins:
  - spring-boot-maven-plugin (run with `mvn spring-boot:run`)
- Project: com.example.ideaboard-backend

---

## 🗄️ Database Files

**`database/schema.sql`**
- MySQL database initialization script
- Creates database: `ideaboard_db`
- Creates table: `ideas` with:
  - 8 columns (id, title, category, description, status, owner_name, created_at, updated_at)
  - Indexes for performance
  - UTF-8 character encoding
- Inserts 5 sample ideas
- Includes verification query

---

## 🛠️ Scripts & Automation

**`launch.bat`** (Windows Batch Script)
- Automated app launcher
- Steps:
  1. Checks MySQL connection
  2. Starts backend in new terminal window
  3. Waits 10 seconds for backend startup
  4. Starts frontend in current window
- Error handling for missing MySQL

**`setup-database.bat`** (Windows Batch Script)
- Database initialization helper
- Prompts user for confirmation
- Runs `schema.sql` via MySQL CLI
- Reports success/failure

---

## 📄 Configuration Files

**`.gitignore`**
- Git exclusion rules
- Ignores:
  - Maven build output (`target/`)
  - IDE files (`.idea/`, `.vscode/`, `.settings/`)
  - Database files (`*.db`, `*.log`)
  - OS files (`.DS_Store`, `Thumbs.db`)
  - Local config (`application-local.properties`)

**`ideaboard.code-workspace`** (VS Code Multi-root Workspace)
- Defines two workspace folders:
  1. Frontend (JavaFX) - root folder
  2. Backend (Spring Boot) - backend folder
- Settings:
  - Java auto-build on save
  - Exclude `target/` from file explorer
  - Source paths configuration
- Recommends VS Code extensions:
  - Java Extension Pack
  - Spring Boot Extension Pack
  - MySQL Client
  - REST Client

**`test-api.http`** (REST Client Format)
- API testing file for VS Code REST Client extension
- Includes sample requests for all 8 endpoints:
  - GET all ideas
  - GET idea by ID
  - POST new idea (with JSON body)
  - PUT update idea (with JSON body)
  - DELETE idea
  - GET by category
  - GET by status
  - GET search
- Click "Send Request" above each section to test

---

## 📚 Documentation Files

**`START_HERE.md`** ⭐ **MAIN OVERVIEW**
- **Read this first!**
- Complete project summary
- What's implemented
- How to run
- Testing guide
- Next steps roadmap
- Troubleshooting
- Quick command reference

**`QUICKSTART.md`**
- Fast setup guide
- Step-by-step commands
- Database setup
- Running backend/frontend
- Quick test procedures
- Common issues

**`PROJECT_OVERVIEW.md`**
- Detailed project documentation
- Architecture diagram
- Full file tree
- API endpoints reference
- Database schema
- Configuration guide
- Development workflow
- Learning resources
- Taiga/GitHub integration

**`README_FULLSTACK.md`**
- Full-stack architecture guide
- Technology stack explanation
- Project structure walkthrough
- Running instructions
- API documentation
- Database schema details
- Testing guide
- Next steps for features
- Troubleshooting reference

**`ARCHITECTURE.md`**
- Visual data flow diagrams
- Request/response flow
- Layer-by-layer explanation
- Technology mapping
- Data transformation journey
- Security & validation flow
- Performance notes
- Component interaction diagrams
- HTTP communication details
- Scalability path

**`README.md`**
- Original JavaFX-focused guide
- Created before backend integration
- Still useful for JavaFX-specific info
- Scene Builder usage
- Modal dialog code examples

**`FILE_GUIDE.md`** (This File)
- Directory structure reference
- Every file explained
- Quick lookup guide
- File purpose descriptions

---

## 📊 File Count Summary

```
Total Files: 32

Source Code:
  - Java files: 10 (Frontend: 3, Backend: 7)
  - FXML files: 1
  - CSS files: 1
  - SQL files: 1

Configuration:
  - pom.xml: 2 (Frontend, Backend)
  - application.properties: 1
  - module-info.java: 1
  - .gitignore: 1
  - *.code-workspace: 1

Scripts:
  - Batch files: 2

Testing:
  - *.http: 1

Documentation:
  - Markdown files: 7

Total Lines of Code: ~1,800
```

---

## 🔍 Quick File Finder

**Need to...**

### Change UI Layout
→ `src/main/resources/com/example/ideaboard/views/create_idea.fxml`

### Change UI Styling
→ `src/main/resources/com/example/ideaboard/styles/app.css`

### Change Form Logic
→ `src/main/java/com/example/ideaboard/controllers/CreateIdeaController.java`

### Add API Endpoint
→ `backend/src/main/java/com/example/ideaboard/controller/IdeaController.java`

### Change Database
→ `backend/src/main/resources/application.properties` (connection)
→ `database/schema.sql` (structure)

### Modify Data Model
→ `backend/src/main/java/com/example/ideaboard/model/Idea.java`

### Test API
→ `test-api.http` (VS Code REST Client)
→ Browser: `http://localhost:8080/api/ideas`

### Setup Database
→ Run `setup-database.bat`
→ Or manually: `mysql -u root -p < database\schema.sql`

### Run Application
→ `launch.bat` (automated)
→ Or manually: backend → `mvn spring-boot:run`, frontend → `mvn javafx:run`

---

## 📋 Files by Priority

### 🔥 Critical (Don't Delete!)
1. `CreateIdeaController.java` - API integration logic
2. `create_idea.fxml` - UI layout
3. `IdeaController.java` - REST endpoints
4. `Idea.java` - Data model
5. `application.properties` - Database config
6. `schema.sql` - Database structure
7. Both `pom.xml` files - Dependencies

### ⭐ Important (Modify Carefully)
1. `app.css` - Styles
2. `MainApp.java` - Application entry
3. `IdeaService.java` - Business logic
4. `IdeaRepository.java` - Data access
5. `module-info.java` - Module config
6. `CorsConfig.java` - CORS settings

### 📄 Helpful (Reference)
1. All documentation files
2. `test-api.http` - API testing
3. `DialogHelper.java` - Utilities

### 🔧 Convenience (Nice to Have)
1. `launch.bat` - Launcher script
2. `setup-database.bat` - Setup helper
3. `.gitignore` - Git config
4. `ideaboard.code-workspace` - VS Code workspace

---

## 🎯 File Modification Guide

### To Add a New Form Field

**Edit 3 Files:**

1. **`create_idea.fxml`** - Add UI control
   ```xml
   <VBox spacing="8" styleClass="form-group">
       <Label text="New Field" styleClass="field-label"/>
       <TextField fx:id="newField"/>
   </VBox>
   ```

2. **`CreateIdeaController.java`** - Add Java field
   ```java
   @FXML
   private TextField newField;
   
   // In createIdea() method, add to JSON:
   "newField": "%s"
   ```

3. **`backend/.../Idea.java`** - Add entity field
   ```java
   @Column(name = "new_field")
   private String newField;
   // + getter/setter
   ```

Database will auto-update (JPA `ddl-auto=update`)!

---

**Use this guide to navigate the codebase quickly! 🚀**
