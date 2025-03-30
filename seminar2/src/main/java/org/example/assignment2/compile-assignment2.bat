@echo off
echo Compiling Assignment2...

REM Set path to the Jazzy JAR (adjust the path if needed)
set JAZZY_JAR=C:\Uni\Cryptography\seminar2\lib\jazzy-core-0.5.2.jar

REM Compile Assignment2.java.
REM The relative path "..\..\..\..\..\..\target\classes" goes from:
REM   src\main\java\org\example\assignment2  up 6 levels -> seminar2, then down to target\classes.
javac -cp %JAZZY_JAR% -d ..\..\..\..\..\..\target\classes Assignment2.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful.
    echo Running Assignment2...
    REM Run the program; use semicolon to separate classpath entries on Windows.
    java -cp ..\..\..\..\..\..\target\classes;%JAZZY_JAR% org.example.assignment2.Assignment2
) else (
    echo Compilation failed.
)
pause
