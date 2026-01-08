@echo off
echo ========================================
echo Stopping HexRide Infrastructure
echo ========================================
echo.

cd /d "%~dp0.."
docker-compose -f docker-compose.infra.yml down

echo.
echo Infrastructure stopped.
pause
