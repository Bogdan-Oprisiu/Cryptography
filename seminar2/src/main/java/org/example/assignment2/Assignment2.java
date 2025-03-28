package org.example.assignment2;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Assignment2 {
    // ******************* FREQUENCY MAPS *******************
    // Single-letter frequencies (in %)
    private static final Map<Character, Double> ENGLISH_FREQ = new HashMap<>();

    static {
        ENGLISH_FREQ.put('A', 8.17);
        ENGLISH_FREQ.put('B', 1.49);
        ENGLISH_FREQ.put('C', 2.78);
        ENGLISH_FREQ.put('D', 4.25);
        ENGLISH_FREQ.put('E', 12.70);
        ENGLISH_FREQ.put('F', 2.23);
        ENGLISH_FREQ.put('G', 2.02);
        ENGLISH_FREQ.put('H', 6.09);
        ENGLISH_FREQ.put('I', 6.97);
        ENGLISH_FREQ.put('J', 0.15);
        ENGLISH_FREQ.put('K', 0.77);
        ENGLISH_FREQ.put('L', 4.03);
        ENGLISH_FREQ.put('M', 2.41);
        ENGLISH_FREQ.put('N', 6.75);
        ENGLISH_FREQ.put('O', 7.51);
        ENGLISH_FREQ.put('P', 1.93);
        ENGLISH_FREQ.put('Q', 0.10);
        ENGLISH_FREQ.put('R', 5.99);
        ENGLISH_FREQ.put('S', 6.33);
        ENGLISH_FREQ.put('T', 9.06);
        ENGLISH_FREQ.put('U', 2.76);
        ENGLISH_FREQ.put('V', 0.98);
        ENGLISH_FREQ.put('W', 2.36);
        ENGLISH_FREQ.put('X', 0.15);
        ENGLISH_FREQ.put('Y', 1.97);
        ENGLISH_FREQ.put('Z', 0.07);
    }

    // Digram frequencies (in %).
    private static final Map<String, Double> ENGLISH_DIGRAM_FREQ = new HashMap<>();

    static {
        ENGLISH_DIGRAM_FREQ.put("TH", 3.15);
        ENGLISH_DIGRAM_FREQ.put("HE", 2.51);
        ENGLISH_DIGRAM_FREQ.put("IN", 1.69);
        ENGLISH_DIGRAM_FREQ.put("ER", 1.54);
        ENGLISH_DIGRAM_FREQ.put("AN", 2.01);
        ENGLISH_DIGRAM_FREQ.put("RE", 1.45);
        ENGLISH_DIGRAM_FREQ.put("ON", 1.38);
        ENGLISH_DIGRAM_FREQ.put("EN", 1.30);
        ENGLISH_DIGRAM_FREQ.put("AT", 1.21);
        ENGLISH_DIGRAM_FREQ.put("ND", 1.18);
        ENGLISH_DIGRAM_FREQ.put("TI", 1.24);
        ENGLISH_DIGRAM_FREQ.put("ES", 1.31);
        ENGLISH_DIGRAM_FREQ.put("OF", 0.92);
        ENGLISH_DIGRAM_FREQ.put("EA", 1.00);
        ENGLISH_DIGRAM_FREQ.put("OU", 0.94);
        ENGLISH_DIGRAM_FREQ.put("IS", 1.06);
        ENGLISH_DIGRAM_FREQ.put("IT", 1.00);
    }

    // Trigram frequencies (in %).
    private static final Map<String, Double> ENGLISH_TRIGRAM_FREQ = new HashMap<>();

    static {
        ENGLISH_TRIGRAM_FREQ.put("THE", 3.83);
        ENGLISH_TRIGRAM_FREQ.put("ING", 1.11);
        ENGLISH_TRIGRAM_FREQ.put("AND", 1.02);
        ENGLISH_TRIGRAM_FREQ.put("ENT", 0.68);
        ENGLISH_TRIGRAM_FREQ.put("ION", 0.75);
        ENGLISH_TRIGRAM_FREQ.put("TIO", 0.72);
        ENGLISH_TRIGRAM_FREQ.put("HER", 0.60);
        ENGLISH_TRIGRAM_FREQ.put("ERE", 0.62);
        ENGLISH_TRIGRAM_FREQ.put("ATE", 0.59);
        ENGLISH_TRIGRAM_FREQ.put("VER", 0.53);
        ENGLISH_TRIGRAM_FREQ.put("THA", 0.52);
        ENGLISH_TRIGRAM_FREQ.put("ATI", 0.51);
        ENGLISH_TRIGRAM_FREQ.put("FOR", 0.51);
        ENGLISH_TRIGRAM_FREQ.put("HAT", 0.50);
        ENGLISH_TRIGRAM_FREQ.put("ERS", 0.49);
        ENGLISH_TRIGRAM_FREQ.put("HIS", 0.47);
        ENGLISH_TRIGRAM_FREQ.put("RES", 0.45);
        ENGLISH_TRIGRAM_FREQ.put("ILL", 0.43);
        ENGLISH_TRIGRAM_FREQ.put("NTH", 0.50);
        ENGLISH_TRIGRAM_FREQ.put("OTH", 0.45);
        ENGLISH_TRIGRAM_FREQ.put("EST", 0.40);
    }

    // Alphabet for Affine (26 uppercase letters)
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // ******************* AFFINE CIPHER UTILS *******************

    // Compute gcd
    private static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    // Compute modular inverse of a mod 26 (return -1 if none)
    private static int modInverse(int a) {
        a %= 26;
        for (int x = 1; x < 26; x++) {
            if ((a * x) % 26 == 1) {
                return x;
            }
        }
        return -1;
    }

    // Decrypt using Affine formula: P = a_inv * (C - b) mod 26
    private static String affineDecrypt(String cipherText, int a, int b) {
        StringBuilder sb = new StringBuilder();
        int aInv = modInverse(a);
        for (char c : cipherText.toCharArray()) {
            int pos = ALPHABET.indexOf(c);
            if (pos != -1) {
                int decPos = (aInv * (pos - b + 26)) % 26;
                sb.append(ALPHABET.charAt(decPos));
            } else {
                // keep non-alphabet chars as-is
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // ******************* FREQUENCY HELPERS *******************

    // Observed letter frequency
    private static Map<Character, Double> getObservedLetterFreq(String text) {
        Map<Character, Integer> countMap = new HashMap<>();
        int totalLetters = 0;
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char upper = Character.toUpperCase(c);
                countMap.put(upper, countMap.getOrDefault(upper, 0) + 1);
                totalLetters++;
            }
        }
        Map<Character, Double> freqMap = new HashMap<>();
        for (Map.Entry<Character, Integer> e : countMap.entrySet()) {
            double percentage = (e.getValue() * 100.0) / totalLetters;
            freqMap.put(e.getKey(), percentage);
        }
        return freqMap;
    }

    // Observed digram frequency
    private static Map<String, Double> getObservedDigramFreq(String text) {
        text = text.toUpperCase().replaceAll("[^A-Z]", "");
        Map<String, Integer> countMap = new HashMap<>();
        int totalDigrams = 0;
        for (int i = 0; i < text.length() - 1; i++) {
            String digram = text.substring(i, i + 2);
            countMap.put(digram, countMap.getOrDefault(digram, 0) + 1);
            totalDigrams++;
        }
        Map<String, Double> freqMap = new HashMap<>();
        for (Map.Entry<String, Integer> e : countMap.entrySet()) {
            double percentage = (e.getValue() * 100.0) / totalDigrams;
            freqMap.put(e.getKey(), percentage);
        }
        return freqMap;
    }

    // Observed trigram frequency
    private static Map<String, Double> getObservedTrigramFreq(String text) {
        text = text.toUpperCase().replaceAll("[^A-Z]", "");
        Map<String, Integer> countMap = new HashMap<>();
        int totalTrigrams = 0;
        for (int i = 0; i < text.length() - 2; i++) {
            String trigram = text.substring(i, i + 3);
            countMap.put(trigram, countMap.getOrDefault(trigram, 0) + 1);
            totalTrigrams++;
        }
        Map<String, Double> freqMap = new HashMap<>();
        for (Map.Entry<String, Integer> e : countMap.entrySet()) {
            double percentage = (e.getValue() * 100.0) / totalTrigrams;
            freqMap.put(e.getKey(), percentage);
        }
        return freqMap;
    }

    // ******************* SCORING FUNCTION *******************

    /**
     * Compare observed frequencies (letters, digrams, trigrams) with known English frequencies.
     * We add min(observed, expected) for each match to get a cumulative score.
     */
    private static double calculateScore(String text) {
        double score = 0.0;

        // 1. Letter frequencies
        Map<Character, Double> obsLetterFreq = getObservedLetterFreq(text);
        for (Map.Entry<Character, Double> e : obsLetterFreq.entrySet()) {
            char letter = e.getKey();
            double observed = e.getValue();
            double expected = ENGLISH_FREQ.getOrDefault(letter, 0.0);
            score += Math.min(observed, expected);
        }

        // 2. Digram frequencies
        Map<String, Double> obsDigramFreq = getObservedDigramFreq(text);
        for (Map.Entry<String, Double> e : obsDigramFreq.entrySet()) {
            String digram = e.getKey();
            double observed = e.getValue();
            double expected = ENGLISH_DIGRAM_FREQ.getOrDefault(digram, 0.0);
            score += Math.min(observed, expected);
        }

        // 3. Trigram frequencies
        Map<String, Double> obsTrigramFreq = getObservedTrigramFreq(text);
        for (Map.Entry<String, Double> e : obsTrigramFreq.entrySet()) {
            String trigram = e.getKey();
            double observed = e.getValue();
            double expected = ENGLISH_TRIGRAM_FREQ.getOrDefault(trigram, 0.0);
            score += Math.min(observed, expected);
        }

        return score;
    }

    // ******************* MAIN HACK METHOD *******************

    /**
     * Tries all valid (a, b) for the Affine cipher, picks the highest-scoring plaintext.
     */
    public static void hackAffine(String cipherText) {
        double bestScore = -1.0;
        int bestA = -1;
        int bestB = -1;
        String bestPlaintext = "";

        // Try all possible a, b where gcd(a, 26) = 1
        for (int a = 1; a < 26; a++) {
            if (gcd(a, 26) != 1) continue;  // 'a' must be coprime with 26
            for (int b = 0; b < 26; b++) {
                // Decrypt with (a, b)
                String candidate = affineDecrypt(cipherText, a, b);
                // Calculate the frequency-based score
                double score = calculateScore(candidate);
                if (score > bestScore) {
                    bestScore = score;
                    bestA = a;
                    bestB = b;
                    bestPlaintext = candidate;
                }
            }
        }

        System.out.println("\nBest candidate found:");
        System.out.println("a = " + bestA + ", b = " + bestB + ", Score = " + bestScore);
        System.out.println("Plaintext: " + bestPlaintext);
    }

    // ******************* MAIN METHOD *******************
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter ciphertext: ");
        String cipherText = scanner.nextLine().toUpperCase();
        hackAffine(cipherText);
    }
}

