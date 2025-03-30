#!/bin/bash
echo "Compiling Assignment1 (and SpellCheckerUtil)..."

# Set JAZZY_JAR to the location in your local Maven repository.
JAZZY_JAR="$HOME/.m2/repository/net/sf/jazzy/jazzy-core/0.5.2/jazzy-core-0.5.2.jar"

# Compile both Assignment1.java and SpellCheckerUtil.java.
# The relative path "../../../../../../target/classes" goes from:
#   src/main/java/org/example/assignment1 up 6 levels to seminar2, then down to target/classes.
javac -cp "$JAZZY_JAR" -d ../../../../../../target/classes Assignment1.java SpellCheckerUtil.java

if [ $? -eq 0 ]; then
    echo "Compilation successful."
    echo "Running Assignment1..."
    # Run the program; note that on Unix systems the classpath separator is a colon.
    java -cp ../../../../../../target/classes:"$JAZZY_JAR" org.example.assignment1.Assignment1
else
    echo "Compilation failed."
fi

read -n 1 -s -r -p "Press any key to continue..."
echo
