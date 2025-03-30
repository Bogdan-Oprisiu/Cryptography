@echo off
echo Compiling Assignment1 (and SpellCheckerUtil)...

REM Set JAZZY_JAR to the location in your local Maven repository.
set "JAZZY_JAR=%USERPROFILE%\.m2\repository\net\sf\jazzy\jazzy-core\0.5.2\jazzy-core-0.5.2.jar"

REM Compile both Assignment1.java and SpellCheckerUtil.java.
REM The relative path "..\..\..\..\..\..\target\classes" goes from:
REM   src\main\java\org\example\assignment1 up 6 levels to seminar2, then down to target\classes.
javac -cp %JAZZY_JAR% -d ..\..\..\..\..\..\target\classes Assignment1.java SpellCheckerUtil.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful.
    echo Running Assignment1...
    REM Run the program; note the semicolon (;) separates classpath entries on Windows.
    java -cp ..\..\..\..\..\..\target\classes;%JAZZY_JAR% org.example.assignment1.Assignment1
) else (
    echo Compilation failed.
)
pause
