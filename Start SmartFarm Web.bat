@echo off
echo ==========================================
echo    SmartFarm Web App is Starting...
echo ==========================================
echo.
echo Open your browser and go to:
echo    http://localhost:8080
echo.
echo Press Ctrl+C to stop the server
echo ==========================================
cd /d "%~dp0"
python -m http.server 8080
pause
