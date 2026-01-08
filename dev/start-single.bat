@echo off
setlocal

set SERVICE=%1
if "%SERVICE%"=="" (
    echo Usage: start-single.bat [service-name]
    echo.
    echo Available services:
    echo   eureka-server
    echo   api-gateway
    echo   user-service
    echo   driver-service
    echo   ride-service
    echo   location-service
    echo   matching-service
    echo   websocket-service
    exit /b 1
)

cd /d "%~dp0../%SERVICE%"
echo Starting %SERVICE%...
call mvn spring-boot:run
