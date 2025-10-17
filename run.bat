@echo off
echo Starting Expenso Finance Manager...
echo.

REM Check if bin directory exists
if not exist "bin" (
    echo Error: Application not compiled!
    echo Please run compile.bat first.
    echo.
    pause
    exit /b 1
)

REM Run the application
java -cp bin com.expenso.Main

pause
