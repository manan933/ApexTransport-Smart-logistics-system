@echo off
setlocal enabledelayedexpansion
title Apex Transport - High-Precision Cyber Freight OS (Port 8080)
color 0A

echo ===============================================================================
echo            APEX TRANSPORT -- ENTERPRISE FREIGHT OPERATING SYSTEM
echo                       High-Precision Autonomous Logistics
echo ===============================================================================
echo.

cd /d "%~dp0"

echo [*] Checking Java Environment...
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [!] ERROR: Java is not installed or not added to PATH.
    echo     Please install Java 17 or higher: https://adoptium.net/
    echo.
    pause
    exit /b 1
)

echo [*] Checking Maven Environment...
call mvn -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [!] ERROR: Maven (mvn) is not installed or not added to PATH.
    echo     Please install Apache Maven: https://maven.apache.org/
    echo.
    pause
    exit /b 1
)

echo [*] Checking and freeing Port 8080...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr /r ":8080\>" 2^>nul') do (
    if not "%%a"=="" (
        echo [*] Stopping previous process on port 8080 (PID %%a)...
        taskkill /F /PID %%a >nul 2>&1
    )
)

echo [*] Launching Browser at http://localhost:8080/login in 5 seconds...
start "" powershell -NoProfile -Command "Start-Sleep -Seconds 5; Start-Process 'http://localhost:8080/login'"

echo.
echo ===============================================================================
echo  Pre-Seeded Test Credentials:
echo   - Transporter / Shipper : vikram@transporter.com  / vikram123
echo   - Pilot Driver          : rajesh@driver.com       / rajesh123
echo   - Master Admin          : admin@apex.com          / admin123
echo ===============================================================================
echo.
echo [*] Starting Spring Boot Server on port 8080...
echo.

call mvn spring-boot:run

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [!] Server exited with an error code: %ERRORLEVEL%
    pause
)
