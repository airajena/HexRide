@echo off
echo ========================================
echo Starting All HexRide Services
echo ========================================
echo.
echo Make sure infrastructure is running first!
echo Run 'start-infra.bat' if not started.
echo.
echo Starting services in order...
echo.

cd /d "%~dp0.."

REM Start Eureka Server (must start first)
echo [1/8] Starting Eureka Server...
start "Eureka Server" cmd /k "cd eureka-server && mvn spring-boot:run"
timeout /t 30 /nobreak > nul

REM Start API Gateway
echo [2/8] Starting API Gateway...
start "API Gateway" cmd /k "cd api-gateway && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

REM Start User Service
echo [3/8] Starting User Service...
start "User Service" cmd /k "cd user-service && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

REM Start Driver Service
echo [4/8] Starting Driver Service...
start "Driver Service" cmd /k "cd driver-service && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

REM Start Ride Service
echo [5/8] Starting Ride Service...
start "Ride Service" cmd /k "cd ride-service && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

REM Start Location Service
echo [6/8] Starting Location Service...
start "Location Service" cmd /k "cd location-service && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

REM Start Matching Service
echo [7/8] Starting Matching Service...
start "Matching Service" cmd /k "cd matching-service && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

REM Start WebSocket Service
echo [8/8] Starting WebSocket Service...
start "WebSocket Service" cmd /k "cd websocket-service && mvn spring-boot:run"

echo.
echo ========================================
echo All services starting in separate windows!
echo ========================================
echo.
echo Endpoints:
echo   Eureka:       http://localhost:8761
echo   API Gateway:  http://localhost:8080
echo   User Service: http://localhost:8081
echo.
echo Wait ~60 seconds for all services to register with Eureka.
echo.
pause
