#!/bin/bash

# Set the source file and output directory.
SOURCE_FILE="src/main/java/org/example/Assignment1.java"
OUT_DIR="out"

# Compile the Java program.
javac -d "$OUT_DIR" "$SOURCE_FILE"
if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi

# Run the compiled program with assertions enabled (-ea).
java -ea -cp "$OUT_DIR" org.example.Assignment1
