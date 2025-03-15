package org.example.assignment2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import static org.example.assignment2.Utils.gcd;

public class Assignment2_1 {
    /*** Affine Cipher Encryption: C = (a * P + b) mod 26 ***/
    public static String Encryption(int a, int b, String plaintext) {
        if (gcd(a, 26) != 1)
            return "Error: gcd(a, 26) != 1. Encryption not possible.";

        char[] result = new char[plaintext.length()];

        for (int i = 0; i < plaintext.length(); i++) {
            char c = plaintext.charAt(i);
            if (Character.isUpperCase(c)) {
                int p = c - 'A';
                int encVal = (a * p + b) % 26;
                result[i] = (char) ('A' + encVal);
            } else if (Character.isLowerCase(c)) {
                int p = c - 'a';
                int encVal = (a * p + b) % 26;
                result[i] = (char) ('a' + encVal);
            } else {
                result[i] = c;
            }
        }
        return new String(result);
    }

    public static void main(String[] args) {
        assert Encryption(5, 3, "Hello").equals("Mxggv") : "Encryption assertion failed";
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Assignment 2.1: Affine Cipher Encryption ===");
        System.out.println("Select input method: Manual (M) or CSV file (C)?");
        String option = scanner.nextLine().trim().toUpperCase();

        if (option.equals("M")) {
            System.out.print("Enter a = ");
            int a = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter b = ");
            int b = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter plaintext: ");
            String plaintext = scanner.nextLine();

            String result = Encryption(a, b, plaintext);
            System.out.println("Ciphertext: " + result);

        } else if (option.equals("C")) {
            File csvFile = new File("C:\\Uni\\Cryptography\\Seminar1\\src" +
                    "\\main\\java\\org\\example\\assignment2\\input1.csv");
            if (!csvFile.exists()) {
                System.out.println("CSV file 'input1.csv' not found.");
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
                        String plaintext = parts[2].trim();

                        System.out.println("Processing record: a = " + a + ", b = " + b
                                + ", plaintext = " + plaintext);
                        String ciphertext = Encryption(a, b, plaintext);
                        System.out.println("Ciphertext: " + ciphertext);
                        System.out.println("------------------------------------");
                    }
                } catch (IOException e) {
                    System.out.println("Error reading 'input1.csv': " + e.getMessage());
                }
            }
        } else {
            System.out.println("Invalid choice. Please restart and choose 'M' or 'C'.");
        }

        scanner.close();
    }
}
