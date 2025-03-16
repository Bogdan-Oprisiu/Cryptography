#!/bin/bash
# Compile Utils.java and Assignment1.java into the "out" folder.
javac -d out Assignment1.java
if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi

# Run the compiled Assignment1 program with assertions enabled (-ea).
java -ea -cp out org.example.assignment1.Assignment1
