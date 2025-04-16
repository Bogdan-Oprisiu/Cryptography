@echo off
REM Compile the RSA.java file
javac RSA.java
IF %ERRORLEVEL% NEQ 0 (
    echo Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)

REM Run the RSA program
java RSA

REM Pause so the window stays open after execution
pause
