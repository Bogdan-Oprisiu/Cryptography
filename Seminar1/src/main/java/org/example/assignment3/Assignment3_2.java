package org.example.assignment3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import static org.example.assignment3.Utils.gcd;
import static org.example.assignment3.Utils.modInverse;

public class Assignment3_2 {
    public static String Decryption(int c11, int c12, int c21, int c22, String ciphertext) {
        int det = c11 * c22 - c12 * c21;
        det = ((det % 26) + 26) % 26;

        if (gcd(det, 26) != 1)
            return "Error: gcd(det,26) != 1. Decryption not possible.";

        int detInv = modInverse(det, 26);
        int i11 = (detInv * c22) % 26;
        int i12 = (detInv * (-c21)) % 26;
        int i21 = (detInv * (-c12)) % 26;
        int i22 = (detInv * c11) % 26;

        if (i11 < 0) i11 += 26;
        if (i12 < 0) i12 += 26;
        if (i21 < 0) i21 += 26;
        if (i22 < 0) i22 += 26;

        String ct = ciphertext.toLowerCase();
        if (ct.length() % 2 != 0)
            ct = ct + "x";

        char[] result = new char[ct.length()];
        for (int i = 0; i < ct.length(); i += 2) {
            int y1 = ct.charAt(i) - 'a';
            int y2 = ct.charAt(i + 1) - 'a';
            int x1 = (i11 * y1 + i21 * y2) % 26;
            int x2 = (i12 * y1 + i22 * y2) % 26;
            if (x1 < 0) x1 += 26;
            if (x2 < 0) x2 += 26;
            result[i] = (char) (x1 + 'a');
            result[i + 1] = (char) (x2 + 'a');
        }
        return new String(result);
    }

    public static void main(String[] args) {
        assert Decryption(11, 3, 8, 7, "xiyj").equals("hill") : "Decryption test failed";
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Assignment 3_2: Hill Cipher Decryption ===");
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
            System.out.print("Enter ciphertext: ");
            String ciphertext = scanner.nextLine();
            String plaintext = Decryption(c11, c12, c21, c22, ciphertext);
            System.out.println("Plaintext: " + plaintext);
        } else if (option.equals("C")) {
            File csvFile = new File("C:\\Uni\\Cryptography\\Seminar1\\src\\main\\java\\org\\example\\assignment3\\input2.csv");
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
                        String ciphertext = parts[4].trim();
                        System.out.println("Processing record: key = [[" + c11 + " " + c12 + "]; [" + c21 + " " + c22 + "]], ciphertext = " + ciphertext);
                        String plaintext = Decryption(c11, c12, c21, c22, ciphertext);
                        System.out.println("Plaintext: " + plaintext);
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
