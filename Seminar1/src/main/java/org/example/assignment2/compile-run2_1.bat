@echo off
set "CURRENT_DIR=%~dp0"
javac -d "%CURRENT_DIR%out" "%CURRENT_DIR%Utils.java" "%CURRENT_DIR%Assignment2_1.java"
if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b %errorlevel%
)
java -ea -cp "%CURRENT_DIR%out" org.example.assignment2.Assignment2_1
pause
