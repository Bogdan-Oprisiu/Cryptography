#!/bin/bash
echo "Compiling all AES source files..."
javac *.java
if [ $? -ne 0 ]; then
    echo "Error: Failed to compile one or more files."
    read -n1 -s -r -p "Press any key to continue..."
    exit 1
fi
echo "All AES source files compiled successfully."

echo "Running AES demo..."
java -cp .. AES.AES
read -n1 -s -r -p "Press any key to continue..."
