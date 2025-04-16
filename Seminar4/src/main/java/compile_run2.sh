#!/bin/bash
# Compile the RSA_hack.java file
echo "Compiling RSA_hack.java..."
javac RSA_hack.java
if [ $? -ne 0 ]; then
    echo "Compilation failed."
    read -p "Press any key to exit..." key
    exit 1
fi

# Run the RSA_hack program
echo "Running RSA_hack..."
java RSA_hack

# Pause so the window stays open after execution
read -p "Press any key to exit..." key
