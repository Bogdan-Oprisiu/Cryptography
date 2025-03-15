#!/bin/bash

# Compile Utils.java and Assignment2_1.java into the "out" folder.
javac -d out Utils.java Assignment2_1.java
if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi

# Run the compiled Assignment2_1 program with assertions enabled (-ea).
java -ea -cp out org.example.assignment2.Assignment2_1
