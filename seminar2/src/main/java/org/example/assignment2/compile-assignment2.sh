#!/bin/bash
echo "Compiling Assignment2..."

# Set path to the Jazzy JAR (adjust the path if needed)
JAZZY_JAR="/c/Uni/Cryptography/seminar2/lib/jazzy-core-0.5.2.jar"

# Compile Assignment2.java.
# The relative path "../../../../../../target/classes" goes from:
#   /c/Uni/Cryptography/seminar2/src/main/java/org/example/assignment2 up 6 levels to seminar2,
# then down into target/classes.
javac -cp "$JAZZY_JAR" -d ../../../../../../target/classes Assignment2.java

if [ $? -eq 0 ]; then
    echo "Compilation successful."
    echo "Running Assignment2..."
    # Run the program; note that on Unix systems the classpath separator is a colon.
    java -cp ../../../../../../target/classes:"$JAZZY_JAR" org.example.assignment2.Assignment2
else
    echo "Compilation failed."
fi

echo "Press any key to continue..."
read -n 1 -s
echo
