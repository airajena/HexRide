@echo off
echo ========================================
echo Stopping All HexRide Services
echo ========================================
echo.
echo This will close all Maven service windows.
echo.

REM Kill all Maven processes running Spring Boot
taskkill /F /FI "WINDOWTITLE eq Eureka Server*" 2>nul
taskkill /F /FI "WINDOWTITLE eq API Gateway*" 2>nul
taskkill /F /FI "WINDOWTITLE eq User Service*" 2>nul
taskkill /F /FI "WINDOWTITLE eq Driver Service*" 2>nul
taskkill /F /FI "WINDOWTITLE eq Ride Service*" 2>nul
taskkill /F /FI "WINDOWTITLE eq Location Service*" 2>nul
taskkill /F /FI "WINDOWTITLE eq Matching Service*" 2>nul
taskkill /F /FI "WINDOWTITLE eq WebSocket Service*" 2>nul

echo.
echo Services stopped.
echo.
echo To also stop infrastructure, run 'stop-infra.bat'
pause
