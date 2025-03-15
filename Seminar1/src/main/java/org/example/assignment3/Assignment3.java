package org.example.assignment3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import static org.example.assignment3.Utils.gcd;

public class Assignment3 {
    public static String Encryption(int c11, int c12, int c21, int c22, String plaintext) {
        int det = c11 * c22 - c12 * c21;
        det %= 26;
        if (det < 0)
            det += 26;
        if (gcd(det, 26) != 1)
            return "Error: gcd(det,26) != 1. Encryption not possible.";

        plaintext = plaintext.replaceAll("[^A-Za-z]", "");

        if (plaintext.length() % 2 == 1)
            plaintext = plaintext + (Character.isLowerCase(plaintext.charAt(0)) ? "a" : "A");
        char[] result = new char[plaintext.length()];

        for (int i = 0; i < plaintext.length(); i += 2) {
            char p1 = plaintext.charAt(i);
            char p2 = plaintext.charAt(i + 1);
            int x1, x2, y1, y2;
            if (Character.isUpperCase(p1) && Character.isUpperCase(p2)) {
                x1 = p1 - 'A';
                x2 = p2 - 'A';
                y1 = (c11 * x1 + c12 * x2) % 26;
                y2 = (c21 * x1 + c22 * x2) % 26;
                if (y1 < 0) y1 += 26;
                if (y2 < 0) y2 += 26;
                result[i] = (char) ('A' + y1);
                result[i + 1] = (char) ('A' + y2);
            } else if (Character.isLowerCase(p1) && Character.isLowerCase(p2)) {
                x1 = p1 - 'a';
                x2 = p2 - 'a';
                y1 = (c11 * x1 + c12 * x2) % 26;
                y2 = (c21 * x1 + c22 * x2) % 26;
                if (y1 < 0) y1 += 26;
                if (y2 < 0) y2 += 26;
                result[i] = (char) ('a' + y1);
                result[i + 1] = (char) ('a' + y2);
            } else if (Character.isAlphabetic(p1) && Character.isAlphabetic(p2)) {
                x1 = Character.toLowerCase(p1) - 'a';
                x2 = Character.toLowerCase(p2) - 'a';
                y1 = (c11 * x1 + c12 * x2) % 26;
                y2 = (c21 * x1 + c22 * x2) % 26;
                if (y1 < 0) y1 += 26;
                if (y2 < 0) y2 += 26;
                result[i] = (char) ('a' + y1);
                result[i + 1] = (char) ('a' + y2);
            }
        }
        return new String(result);
    }

    public static void main(String[] args) {
        assert Encryption(11, 12, 8, 9, "hello").equals("votfyi") : "Encryption test failed";
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Assignment 3_1: Hill Cipher Encryption ===");
        System.out.println("Select input method: Manual (M) or CSV file (C)?");
        String option = scanner.nextLine().trim().toUpperCase();
        if (option.equals("M")) {
            System.out.print("Enter c11 = ");
            int c11 = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter c12 = ");
            int c12 = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter c21 = ");
            int c21 = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter c22 = ");
            int c22 = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter plaintext: ");
            String plaintext = scanner.nextLine();
            String ciphertext = Encryption(c11, c12, c21, c22, plaintext);
            System.out.println("Ciphertext: " + ciphertext);
        } else if (option.equals("C")) {
            File csvFile = new File("C:\\Uni\\Cryptography\\Seminar1\\src\\main\\java\\org\\example\\assignment3\\input1.csv");
            if (!csvFile.exists()) {
                System.out.println("CSV file not found.");
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
                        if (parts.length < 5)
                            continue;
                        int c11 = Integer.parseInt(parts[0].trim());
                        int c12 = Integer.parseInt(parts[1].trim());
                        int c21 = Integer.parseInt(parts[2].trim());
                        int c22 = Integer.parseInt(parts[3].trim());
                        String plaintext = parts[4].trim();
                        System.out.println("Processing record: key = [[" + c11 + " " + c12 + "]; [" + c21 + " " + c22 + "]], plaintext = " + plaintext);
                        String ciphertext = Encryption(c11, c12, c21, c22, plaintext);
                        System.out.println("Ciphertext: " + ciphertext);
                        System.out.println("------------------------------------");
                    }
                } catch (IOException e) {
                    System.out.println("Error reading CSV file: " + e.getMessage());
                }
            }
        } else {
            System.out.println("Invalid choice.");
        }
        scanner.close();
    }
}
