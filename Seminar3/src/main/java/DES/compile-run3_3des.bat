@echo off
echo Compiling TripleDES.java...
javac TripleDES.java
if %errorlevel% neq 0 (
    echo Error: Failed to compile TripleDES.java.
    pause
    exit /b %errorlevel%
)
echo TripleDES.java compiled successfully.

echo Running TripleDES program...
java -cp .. DES.TripleDES
pause
