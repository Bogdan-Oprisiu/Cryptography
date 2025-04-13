#!/bin/bash
echo "Compiling DES.java..."
javac DES.java
if [ $? -ne 0 ]; then
    echo "Error: Failed to compile DES.java."
    read -n1 -s -r -p "Press any key to continue..."
    exit 1
fi
echo "DES.java compiled successfully."

echo "Running DES program..."
java -cp .. DES.DES
read -n1 -s -r -p "Press any key to continue..."
