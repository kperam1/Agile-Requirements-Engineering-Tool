## Requirements

Before running this project, ensure you have the following installed:

### 1. Java 21 or higher

**Check your Java version:**
java -version

**Expected output:** Should show Java 21.x or higher

**If not installed:**
- **macOS (Homebrew):** `brew install openjdk@21`
- **Windows:** Download from [Oracle JDK](https://www.oracle.com/java/technologies/downloads/#java21) or use `choco install openjdk21`
- **Linux (Ubuntu/Debian):** `sudo apt-get install openjdk-21-jdk`

---

### 2. Maven 3.8+

**Check your Maven version:**
mvn --version

**Expected output:** Should show Maven 3.8.0 or higher

**If not installed:**
- **macOS (Homebrew):** `brew install maven`
- **Windows:** Download from [Maven](https://maven.apache.org/download.cgi) or use `choco install maven`
- **Linux (Ubuntu/Debian):** `sudo apt-get install maven`

---

### 3. MySQL 8.0 or higher

**Check your MySQL version:**
mysql --version

**Expected output:** Should show MySQL 8.0 or higher

**If not installed:**
- **macOS (Homebrew):** `brew install mysql`
- **Windows:** Download from [MySQL](https://dev.mysql.com/downloads/mysql/)
- **Linux (Ubuntu/Debian):** `sudo apt-get install mysql-server`

**Start MySQL service:**
- **macOS:** `brew services start mysql`
- **Windows:** Use MySQL Installer or Services panel
- **Linux:** `sudo systemctl start mysql`

---

### 4. JavaFX SDK 21

**Check if JavaFX is available:** (typically installed via Maven dependency)

**If needed manually:**
- Download from [JavaFX SDK](https://gluonhq.com/products/javafx/)
- Set `JAVAFX_HOME` environment variable to the SDK location

---

## Getting Started

### Step 1: Clone the project

git clone https://github.com/kperam1/Agile-Requirements-Engineering-Tool.git
cd Agile-Requirements-Engineering-Tool

### Step 2: Build the project

mvn clean install -DskipTests

### Step 3: Start the Spring Boot backend

Open a terminal and run:

cd backend
mvn spring-boot:run

### Step 4: Start the JavaFX frontend

Open a **new terminal** (keeping the backend running) and run:

mvn javafx:run

**Expected output:** JavaFX application window will launch

---

