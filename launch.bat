@echo off
REM Quick launch script for IdeaBoard Application

echo ========================================
echo IdeaBoard Application Launcher
echo ========================================
echo.

REM Check if MySQL is running
echo [1/3] Checking MySQL connection...
mysql -u root -e "SELECT 1" >nul 2>&1
if errorlevel 1 (
    echo [ERROR] MySQL is not running!
    echo Please start MySQL server first.
    pause
    exit /b 1
)
echo [OK] MySQL is running

echo.
echo [2/3] Starting Spring Boot Backend...
echo Opening new terminal for backend...
start "IdeaBoard Backend" cmd /k "cd backend && mvn spring-boot:run"

REM Wait a bit for backend to start
timeout /t 10 /nobreak >nul

echo.
echo [3/3] Starting JavaFX Frontend...
echo.
mvn clean javafx:run

echo.
echo Application stopped.
pause
