@echo off
REM Compile the RSA_hack.java file
javac RSA_hack.java
IF %ERRORLEVEL% NEQ 0 (
    echo Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)

REM Run the RSA_hack program
java RSA_hack

REM Pause so the window stays open after execution
pause
