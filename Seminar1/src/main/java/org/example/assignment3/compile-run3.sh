#!/bin/bash
# Compile Assignment3.java and Utils.java into the "out" directory.
javac -d out Utils.java Assignment3.java
if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi

# Run the compiled program with assertions enabled (-ea).
java -ea -cp out org.example.assignment3.Assignment3
