@echo off
echo ========================================
echo Building HexRide (Skip Tests)
echo ========================================
echo.

cd /d "%~dp0.."
call mvn clean install -DskipTests

echo.
echo Build complete!
pause
