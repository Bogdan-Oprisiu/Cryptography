@echo off
echo Compiling all AES source files...
javac *.java
if %errorlevel% neq 0 (
    echo Error: Failed to compile one or more files.
    pause
    exit /b %errorlevel%
)
echo All AES source files compiled successfully.

echo Running AES demo...
java -cp .. AES.AES
pause
