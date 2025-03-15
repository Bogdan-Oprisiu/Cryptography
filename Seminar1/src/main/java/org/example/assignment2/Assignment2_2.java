package org.example.assignment2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import static org.example.assignment2.Utils.gcd;
import static org.example.assignment2.Utils.modInverse;

public class Assignment2_2 {
    /*** Affine Cipher Decryption: P = a_inv * (C - b) mod 26 ***/
    public static String Decryption(int a, int b, String ciphertext) {
        if (gcd(a, 26) != 1)
            return "Error: gcd(a, 26) != 1. Decryption not possible.";

        int a_inv = modInverse(a, 26);
        if (a_inv == -1)
            return "Error: No modular inverse found. Decryption not possible.";

        char[] result = new char[ciphertext.length()];

        for (int i = 0; i < ciphertext.length(); i++) {
            char c = ciphertext.charAt(i);
            if (Character.isUpperCase(c)) {
                int valC = c - 'A';
                int decVal = (a_inv * (valC - b + 26)) % 26;
                result[i] = (char) ('A' + decVal);
            } else if (Character.isLowerCase(c)) {
                int valC = c - 'a';
                int decVal = (a_inv * (valC - b + 26)) % 26;
                result[i] = (char) ('a' + decVal);
            } else {
                result[i] = c;
            }
        }
        return new String(result);
    }

    public static void main(String[] args) {
        assert Decryption(7, 9, "GLIIDMGLYL").equals("HELLOTHERE") : "Decryption assertion failed";
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Assignment 2.2: Affine Cipher Decryption ===");
        System.out.println("Select input method: Manual (M) or CSV file (C)?");
        String option = scanner.nextLine().trim().toUpperCase();

        if (option.equals("M")) {
            System.out.print("Enter a = ");
            int a = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter b = ");
            int b = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter ciphertext: ");
            String ciphertext = scanner.nextLine();

            String result = Decryption(a, b, ciphertext);
            System.out.println("Plaintext: " + result);

        } else if (option.equals("C")) {
            File csvFile = new File("C:\\Uni\\Cryptography\\Seminar1\\src" +
                    "\\main\\java\\org\\example\\assignment2\\input2.csv");
            if (!csvFile.exists()) {
                System.out.println("CSV file 'input2.csv' not found.");
            } else {
                try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                    String line;
                    boolean firstLine = true;
                    while ((line = br.readLine()) != null) {
                        if (firstLine) {
                            firstLine = false;
                            continue;
                        }
                        String[] parts = line.split(",");
                        if (parts.length < 3)
                            continue;

                        int a = Integer.parseInt(parts[0].trim());
                        int b = Integer.parseInt(parts[1].trim());
                        String ciphertext = parts[2].trim();

                        System.out.println("Processing record: a = " + a + ", b = " + b
                                + ", ciphertext = " + ciphertext);
                        String plaintext = Decryption(a, b, ciphertext);
                        System.out.println("Plaintext: " + plaintext);
                        System.out.println("------------------------------------");
                    }
                } catch (IOException e) {
                    System.out.println("Error reading 'input2.csv': " + e.getMessage());
                }
            }
        } else {
            System.out.println("Invalid choice. Please restart and choose 'M' or 'C'.");
        }

        scanner.close();
    }
}
