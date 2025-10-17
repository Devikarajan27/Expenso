@echo off
echo Compiling Expenso Finance Manager...
echo.

REM Create bin directory if it doesn't exist
if not exist "bin" mkdir bin

REM Compile all Java files (excluding GmailConnector.java which requires additional dependencies)
javac -d bin -sourcepath src src\com\expenso\*.java src\com\expenso\model\*.java src\com\expenso\data\*.java src\com\expenso\ui\ExpensoApp.java src\com\expenso\ui\ChartPanel.java src\com\expenso\ui\ImportTransactionsDialog.java src\com\expenso\ui\UpiPaymentDialog.java src\com\expenso\util\BankStatementParser.java src\com\expenso\util\EmailTransactionParser.java src\com\expenso\util\QRCodeGenerator.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Compilation successful!
    echo ========================================
    echo.
    echo To run the application, use: run.bat
    echo.
) else (
    echo.
    echo ========================================
    echo Compilation failed!
    echo ========================================
    echo.
)

pause
