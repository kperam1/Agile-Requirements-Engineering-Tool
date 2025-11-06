# Quick Start Commands

## 🗄️ Database Setup (First Time)

### Option 1: Using MySQL Workbench
1. Open MySQL Workbench
2. Connect to your MySQL server
3. File → Open SQL Script → Select `database/schema.sql`
4. Execute the script (⚡ button)

### Option 2: Using Command Line
```powershell
# Windows PowerShell
mysql -u root -p < database\schema.sql
```

## 🚀 Running the Application

### Step 1: Start Backend (Terminal 1)
```powershell
cd backend
mvn clean install
mvn spring-boot:run
```

**Wait for:** `IdeaBoard Backend API is running!`

### Step 2: Start Frontend (Terminal 2)
```powershell
# From root 'idea' folder
mvn clean javafx:run
```

## 🧪 Quick Test

### Test Backend API
```powershell
# Check if backend is running
Invoke-WebRequest -Uri http://localhost:8080/api/ideas -Method GET

# Or open in browser:
# http://localhost:8080/api/ideas
```

### Test Frontend
1. Click "Open Create Idea Form"
2. Fill in Title: "Test Idea"
3. Click "Create Idea"
4. Should see success message!

## 📝 Database Credentials

**Default settings** (in `backend/src/main/resources/application.properties`):
- Database: `ideaboard_db`
- Username: `root`
- Password: *(empty)*
- Port: `3306`

**To change password:**
Edit `application.properties`:
```properties
spring.datasource.password=your_password_here
```

## 🛑 Stopping the Application

- **Backend**: Press `Ctrl+C` in backend terminal
- **Frontend**: Close JavaFX window
- **MySQL**: Keep running (or stop via MySQL Workbench)

## 📊 View Data in MySQL

```sql
USE ideaboard_db;
SELECT * FROM ideas ORDER BY created_at DESC;
```

## 🔧 Troubleshooting

**Backend won't start?**
```powershell
# Check if port 8080 is in use
netstat -ano | findstr :8080

# Kill process if needed (replace PID)
taskkill /PID <PID> /F
```

**MySQL connection failed?**
1. Start MySQL service (MySQL Workbench → Server → Start Server)
2. Verify credentials in `application.properties`

**Frontend can't connect?**
1. Make sure backend is running
2. Visit http://localhost:8080/api/ideas in browser
3. Should see JSON response

## 🎯 Development Workflow

1. Start MySQL (once)
2. Start Backend (terminal 1)
3. Start Frontend (terminal 2)
4. Make changes
5. Backend auto-reloads (Spring DevTools)
6. Frontend: stop and restart `mvn javafx:run`

---

**Need help?** Check `README_FULLSTACK.md` for detailed documentation.
