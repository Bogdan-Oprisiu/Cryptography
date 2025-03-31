package org.example.assignment3;

import java.util.*;

public class Assignment3 {
    // Standard alphabet (26 uppercase letters)
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * Part (b): Encrypts plaintext using a Caesar cipher with a fixed shift of 3.
     */
    private static String caesarEncrypt(String plaintext) {
        StringBuilder cipherText = new StringBuilder();
        for (int i = 0; i < plaintext.length(); i++) {
            char c = plaintext.charAt(i);
            int idx = ALPHABET.indexOf(c);
            if (idx != -1) {
                int newIdx = (idx + 3) % ALPHABET.length();
                cipherText.append(ALPHABET.charAt(newIdx));
            } else {
                // Keep any non-alphabet characters unchanged.
                cipherText.append(c);
            }
        }
        return cipherText.toString();
    }

    /**
     * Builds a mapping for a 5-bit A/D converter.
     * Here we assign each letter a voltage value spanning from -1 V (for A) to +1 V (for a full-scale 5-bit value),
     * but we only map the 26 letters. For our purposes, this means:
     *   Voltage for A = -1.0 V
     *   Voltage for B = -1.0 + (1/16) = -0.9375 V
     *   ...
     *   Voltage for Z = -1.0 + 25*(1/16) = 0.5625 V
     * The step size is (endVoltage - startVoltage) / 32 = (1 - (-1)) / 32 = 2/32 = 0.0625 V.
     */
    private static Map<Character, Double> getSignalMapping5Bit() {
        Map<Character, Double> mapping = new HashMap<>();
        double startVoltage = -1.0;  // Voltage for 00000 (A)
        double endVoltage = 1.0;     // Voltage for 11111 (unused)
        double step = (endVoltage - startVoltage) / 32; // 2/32 = 0.0625 V
        for (int i = 0; i < ALPHABET.length(); i++) {
            mapping.put(ALPHABET.charAt(i), startVoltage + i * step);
        }
        return mapping;
    }

    /**
     * Converts a ciphertext string into a list of analog voltage values using the provided mapping.
     */
    private static List<Double> encryptToAnalog(String ciphertext, Map<Character, Double> mapping) {
        List<Double> analogSignal = new ArrayList<>();
        for (int i = 0; i < ciphertext.length(); i++) {
            char c = ciphertext.charAt(i);
            // For characters not in our mapping, default to 0 V.
            if (mapping.containsKey(c)) {
                analogSignal.add(mapping.get(c));
            } else {
                analogSignal.add(0.0);
            }
        }
        return analogSignal;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // ---------------- Part (a) ----------------
        // A 3-bit A/D converter provides 2^3 = 8 levels.
        int levels3Bit = (int) Math.pow(2, 3);
        System.out.println("Part (a): A 3-bit A/D converter uses " + levels3Bit + " levels.");

        // ---------------- Part (b) ----------------
        System.out.print("Enter plaintext: ");
        String plaintext = scanner.nextLine().toUpperCase();

        // Encrypt the plaintext using a Caesar cipher (with a shift of 3).
        String ciphertext = caesarEncrypt(plaintext);
        System.out.println("Ciphertext: " + ciphertext);

        // Build the 5-bit A/D converter mapping for the 26 letters.
        Map<Character, Double> signalMapping = getSignalMapping5Bit();
        System.out.println("Voltage Mapping (5-bit A/D): " + signalMapping);

        // Convert the ciphertext into an analog signal using the mapping.
        List<Double> analogSignal = encryptToAnalog(ciphertext, signalMapping);
        System.out.println("Encrypted Analog Signal: " + analogSignal);
    }
}
