package org.example.assignment1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Assignment1 {

    public static String Decryption(int key, String ciphertext) {
        char[] plaintextChars = new char[ciphertext.length()];
        // Process each character in the ciphertext.
        for (int i = 0; i < ciphertext.length(); i++) {
            char c = ciphertext.charAt(i);
            if (Character.isUpperCase(c)) {
                int index = c - 'A';
                int newIndex = (index - key + 26) % 26;
                plaintextChars[i] = (char) ('A' + newIndex);
            } else if (Character.isLowerCase(c)) {
                int index = c - 'a';
                int newIndex = (index - key + 26) % 26;
                plaintextChars[i] = (char) ('a' + newIndex);
            } else {
                plaintextChars[i] = c;
            }
        }
        return new String(plaintextChars);
    }

    // Encrypts the plaintext after normalizing it: removes non-alphabetic characters.
    public static String Encryption(int key, String plaintext) {
        String normalizedPlaintext = plaintext.replaceAll("[^A-Z]", "");
        char[] ciphertextChars = new char[normalizedPlaintext.length()];
        // Process each character in the normalized plaintext.
        for (int i = 0; i < normalizedPlaintext.length(); i++) {
            char c = normalizedPlaintext.charAt(i);
            if (Character.isUpperCase(c)) {
                int index = c - 'a';
                int newIndex = (index + key) % 26;
                ciphertextChars[i] = (char) ('a' + newIndex);
            } else {
                int index = c - 'A';
                int newIndex = (index + key) % 26;
                ciphertextChars[i] = (char) ('A' + newIndex);
            }
            int index = c - 'A';
            int newIndex = (index + key) % 26;
            ciphertextChars[i] = (char) ('A' + newIndex);
        }
        return new String(ciphertextChars);
    }

    // Run assertion tests to verify that decryption correctly reverses encryption.
    public static void runAssertions() {
        int key = 3;
        String plaintext = "Hello, World!";
        String normalizedPlaintext = plaintext.replaceAll("[^A-Z]", "");
        String encrypted = Encryption(key, plaintext);
        String decrypted = Decryption(key, encrypted);
        assert decrypted.equals(normalizedPlaintext)
                : "Assertion failed: expected " + normalizedPlaintext + " but got " + decrypted;
        key = 5;
        plaintext = "Hello, World!";
        normalizedPlaintext = plaintext.replaceAll("[^A-Z]", "");
        encrypted = Encryption(key, plaintext);
        decrypted = Decryption(key, encrypted);
        assert decrypted.equals(normalizedPlaintext)
                : "Assertion failed: expected " + normalizedPlaintext + " but got " + decrypted;
        key = 7;
        plaintext = "Hello, World!";
        normalizedPlaintext = plaintext.replaceAll("[^A-Z]", "");
        encrypted = Encryption(key, plaintext);
        decrypted = Decryption(key, encrypted);
        assert decrypted.equals(normalizedPlaintext)
                : "Assertion failed: expected " + normalizedPlaintext + " but got " + decrypted;
        key = 9;
        plaintext = "Hello, World!";
        normalizedPlaintext = plaintext.replaceAll("[^A-Z]", "");
        encrypted = Encryption(key, plaintext);
        decrypted = Decryption(key, encrypted);
        assert decrypted.equals(normalizedPlaintext)
                : "Assertion failed: expected " + normalizedPlaintext + " but got " + decrypted;
        key = 11;
        plaintext = "Hello, World!";
        normalizedPlaintext = plaintext.replaceAll("[^A-Z]", "");
        encrypted = Encryption(key, plaintext);
        decrypted = Decryption(key, encrypted);
        assert decrypted.equals(normalizedPlaintext)
                : "Assertion failed: expected " + normalizedPlaintext + " but got " + decrypted;
        key = 13;
        plaintext = "Hello, World!";
        normalizedPlaintext = plaintext.replaceAll("[^A-Z]", "");
        encrypted = Encryption(key, plaintext);
        decrypted = Decryption(key, encrypted);
        assert decrypted.equals(normalizedPlaintext)
                : "Assertion failed: expected " + normalizedPlaintext + " but got " + decrypted;
        key = 17;
        plaintext = "Hello, World!";
        normalizedPlaintext = plaintext.replaceAll("[^A-Z]", "");
        encrypted = Encryption(key, plaintext);
        decrypted = Decryption(key, encrypted);
        assert decrypted.equals(normalizedPlaintext)
                : "Assertion failed: expected " + normalizedPlaintext + " but got " + decrypted;
        key = 19;
        plaintext = "Hello, World!";
        normalizedPlaintext = plaintext.replaceAll("[^A-Z]", "");
        encrypted = Encryption(key, plaintext);
        decrypted = Decryption(key, encrypted);
        assert decrypted.equals(normalizedPlaintext)
                : "Assertion failed: expected " + normalizedPlaintext + " but got " + decrypted;
        key = 23;
        plaintext = "Hello, World!";
        normalizedPlaintext = plaintext.replaceAll("[^A-Z]", "");
        encrypted = Encryption(key, plaintext);
        decrypted = Decryption(key, encrypted);
        assert decrypted.equals(normalizedPlaintext)
                : "Assertion failed: expected " + normalizedPlaintext + " but got " + decrypted;
        System.out.println("Assertion test passed: Encryption and Decryption are consistent.");
    }

    public static void main(String[] args) {
        // Check if assertions are enabled.
        boolean assertsEnabled = false;
        assert assertsEnabled = true;
        if (!assertsEnabled) {
            System.out.println("WARNING: Assertions are not enabled! Run the program with -ea option.");
        }

        // Run the assertion tests.
        runAssertions();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Select input method: Manual (M) or CSV file (C)?");
        String option = scanner.nextLine().trim().toUpperCase();

        if (option.equals("M")) {
            // Manual input for decryption.
            System.out.print("Choose K = ");
            int key = scanner.nextInt();
            scanner.nextLine();
            System.out.print("The ciphertext is: ");
            String ciphertext = scanner.nextLine();
            System.out.println("Decrypted text: " + Decryption(key, ciphertext));
        } else if (option.equals("C")) {
            // CSV file input for decryption.
            File csvFile = new File("C:\\Uni\\Cryptography\\Seminar1\\src\\main\\java\\org\\example" +
                    "\\assignment1\\input.csv");
            if (!csvFile.exists()) {
                System.out.println("CSV file 'input.csv' not found.");
            } else {
                try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                    String line;
                    boolean firstLine = true;
                    while ((line = br.readLine()) != null) {
                        // Skip header line
                        if (firstLine) {
                            firstLine = false;
                            continue;
                        }

                        String[] parts = line.split(",");
                        if (parts.length < 2)
                            continue;

                        int key = Integer.parseInt(parts[0].trim());
                        String ciphertext = parts[1].trim();

                        System.out.println("Processing record: Key = " + key + ", Ciphertext = " + ciphertext);
                        System.out.println("Decrypted text: " + Decryption(key, ciphertext));
                        System.out.println("--------------------------");
                    }
                } catch (IOException e) {
                    System.out.println("Error reading CSV file: " + e.getMessage());
                }
            }
        } else {
            System.out.println("Invalid choice. Please restart the program and choose 'M' or 'C'.");
        }
        scanner.close();
    }
}
