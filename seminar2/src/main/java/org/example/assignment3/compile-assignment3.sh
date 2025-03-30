#!/bin/bash
echo "Compiling Assignment3..."

# Compile Assignment3.java; output goes to target/classes folder.
javac -d ../../../../../../target/classes Assignment3.java

if [ $? -eq 0 ]; then
    echo "Compilation successful."
    echo "Running Assignment3..."
    java -cp ../../../../../../target/classes org.example.assignment3.Assignment3
else
    echo "Compilation failed."
fi

read -n 1 -s -r -p "Press any key to continue..."
echo
