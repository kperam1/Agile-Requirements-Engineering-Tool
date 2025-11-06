@echo off
REM Script to initialize the database

echo ========================================
echo IdeaBoard Database Setup
echo ========================================
echo.

echo This will create/reset the ideaboard_db database.
echo.
set /p confirm="Continue? (Y/N): "

if /i not "%confirm%"=="Y" (
    echo Setup cancelled.
    pause
    exit /b 0
)

echo.
echo Executing schema.sql...
mysql -u root -p < database\schema.sql

if errorlevel 1 (
    echo.
    echo [ERROR] Database setup failed!
    echo Please check your MySQL connection and credentials.
    pause
    exit /b 1
)

echo.
echo [SUCCESS] Database setup completed!
echo.
echo Database: ideaboard_db
echo Sample data: 5 ideas inserted
echo.
pause
