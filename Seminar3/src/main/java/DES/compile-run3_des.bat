@echo off
echo Compiling DES.java...
javac DES.java
if %errorlevel% neq 0 (
    echo Error: Failed to compile DES.java.
    pause
    exit /b %errorlevel%
)
echo DES.java compiled successfully.

echo Running DES program...
java -cp .. DES.DES
pause
