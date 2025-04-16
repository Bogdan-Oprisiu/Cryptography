#!/bin/bash
# Compile the RSA.java file
echo "Compiling RSA.java..."
javac RSA.java
if [ $? -ne 0 ]; then
    echo "Compilation failed."
    read -p "Press any key to exit..." key
    exit 1
fi

# Run the RSA program
echo "Running RSA..."
java RSA

# Pause so the window stays open after execution
read -p "Press any key to exit..." key
