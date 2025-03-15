@echo off
REM 1) Set CURRENT_DIR to the directory where this batch file is located.
set "CURRENT_DIR=%~dp0"

REM 2) Compile Assignment1.java located in the current directory.
javac -d "%CURRENT_DIR%out" "%CURRENT_DIR%Assignment1.java"
if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b %errorlevel%
)

REM 3) Run the compiled program with assertions enabled (-ea).
java -ea -cp "%CURRENT_DIR%out" org.example.assignment1.Assignment1

pause
