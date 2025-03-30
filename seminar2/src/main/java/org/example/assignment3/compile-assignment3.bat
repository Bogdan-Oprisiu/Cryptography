@echo off
echo Compiling Assignment3...

REM Compile Assignment3.java; the output goes to the target\classes folder.
javac -d ..\..\..\..\..\..\target\classes Assignment3.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful.
    echo Running Assignment3...
    REM Run the program (adjust package name if needed)
    java -cp ..\..\..\..\..\..\target\classes org.example.assignment3.Assignment3
) else (
    echo Compilation failed.
)
pause
