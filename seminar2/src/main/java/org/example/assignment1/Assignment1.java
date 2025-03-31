package org.example.assignment1;

import java.util.*;

public class Assignment1 {
    // English letter frequencies (percentages)
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

    // Threshold for acceptable valid-word ratio from the spell checker.
    private static final double VALID_RATIO_THRESHOLD = 0.7;

    // Fixed alphabet: English letters A-Z.
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * Decrypts the ciphertext using a given key and alphabet.
     */
    public static String decryptUsingKey(String cipherText, int key, String alphabet) {
        StringBuilder plainText = new StringBuilder();
        for (int i = 0; i < cipherText.length(); i++) {
            char ch = cipherText.charAt(i);
            int charPos = alphabet.indexOf(ch);
            if (charPos != -1) {
                int newPos = (charPos - key + alphabet.length()) % alphabet.length();
                plainText.append(alphabet.charAt(newPos));
            } else {
                // Keep characters not in our alphabet unchanged.
                plainText.append(ch);
            }
        }
        return plainText.toString();
    }

    /**
     * Segments a candidate plaintext into words using a greedy algorithm.
     * It uses the provided SpellCheckerUtil to verify if a substring is a valid word.
     * If the candidate is a number (digits only), it is automatically considered valid.
     * If no valid word is found at a given starting index, it takes one character and moves on.
     */
    public static String[] segmentText(String text, SpellCheckerUtil spellChecker) {
        List<String> words = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            boolean found = false;
            // Try to find the longest valid word starting at 'start'
            for (int end = text.length(); end > start; end--) {
                String candidate = text.substring(start, end);
                // If candidate is all digits or a valid English word, accept it.
                if (candidate.matches("\\d+") || spellChecker.isEnglishWord(candidate)) {
                    words.add(candidate);
                    start = end;
                    found = true;
                    break;
                }
            }
            // If no valid word is found, add one character and move on.
            if (!found) {
                words.add(text.substring(start, start + 1));
                start++;
            }
        }
        return words.toArray(new String[0]);
    }

    /**
     * Calculates a frequency-based score for the candidate text.
     * This score compares observed letter frequencies with typical English frequencies and adds
     * bonus points for containing common English words.
     */
    private static double calculateScore(String text) {
        double score = 0;
        int letterCount = 0;
        Map<Character, Integer> freq = new HashMap<>();

        // Count the frequency of each letter.
        for (char c : text.toUpperCase().toCharArray()) {
            if (Character.isLetter(c)) {
                freq.put(c, freq.getOrDefault(c, 0) + 1);
                letterCount++;
            }
        }

        // Compare the observed frequency with expected English frequencies.
        if (letterCount > 0) {
            for (Map.Entry<Character, Integer> entry : freq.entrySet()) {
                double observed = (entry.getValue() * 100.0) / letterCount;
                double expected = ENGLISH_FREQ.getOrDefault(entry.getKey(), 0.0);
                score += Math.min(observed, expected);
            }
        }

        // Bonus points if common English words are found.
        String[] commonWords = {"THE", "AND", "THAT", "HAVE", "FOR", "NOT", "WITH"};
        String upperText = text.toUpperCase();
        for (String word : commonWords) {
            if (upperText.contains(word)) {
                score += 5;
            }
        }

        // Numbers are not penalized.
        for (char c : text.toCharArray()) {
            if (Character.isDigit(c)) {
                score += 1;
            }
        }

        return score;
    }

    /**
     * Combines frequency scoring with spell-checker segmentation.
     * It tries every possible key for multiple alphabets (letters plus digits 1 to 9),
     * and uses both the frequency score and the ratio of valid segmented words.
     */
    public static void HackShift(String cipherText) {
        // Initialize the spell checker. Make sure the dictionary file path is correct for your environment.
        SpellCheckerUtil spellCheckerUtil = new SpellCheckerUtil("src/main/resources/english.0");

        // We'll build up the alphabet cumulatively here:
        String extendedAlphabet = ALPHABET; // start with just A-Z

        // Loop from digit 1..9, each time adding that digit to the existing extendedAlphabet
        for (int digit = 0; digit <= 9; digit++) {
            extendedAlphabet += digit;
            System.out.println("----- For Extended Alphabet: " + extendedAlphabet + " -----");

            // Tracking variables for this cumulatively extendedAlphabet:
            int bestKeyForThisAlphabet = -1;
            double bestFrequencyScoreForThisAlphabet = -1;
            double bestValidRatioForThisAlphabet = -1;
            String bestCandidateForThisAlphabet = "";

            int bestKeyOverallForThisAlphabet = -1;
            double bestFrequencyScoreOverallForThisAlphabet = -1;
            String bestCandidateOverallForThisAlphabet = "";

            boolean candidateMeetingThresholdExistsForThisAlphabet = false;

            // Try every possible key against the newly extended alphabet
            for (int key = 0; key < extendedAlphabet.length(); key++) {
                String candidate = decryptUsingKey(cipherText, key, extendedAlphabet);
                double freqScore = calculateScore(candidate);

                // Use segmentation to check valid words
                String[] segmentedWords = segmentText(candidate, spellCheckerUtil);
                double validRatio = 0.0;
                if (segmentedWords.length > 0) {
                    int validCount = 0;
                    for (String word : segmentedWords) {
                        // Count as valid if it’s all digits or recognized by the spell checker
                        if (!word.isEmpty() && (word.matches("\\d+") || spellCheckerUtil.isEnglishWord(word))) {
                            validCount++;
                        }
                    }
                    validRatio = (double) validCount / segmentedWords.length;
                }

                // Update best overall candidate (by frequency) for this alphabet so far
                if (freqScore > bestFrequencyScoreOverallForThisAlphabet) {
                    bestFrequencyScoreOverallForThisAlphabet = freqScore;
                    bestKeyOverallForThisAlphabet = key;
                    bestCandidateOverallForThisAlphabet = candidate;
                }

                // If it meets the threshold, track the best among those that pass
                if (validRatio >= VALID_RATIO_THRESHOLD) {
                    candidateMeetingThresholdExistsForThisAlphabet = true;
                    if (freqScore > bestFrequencyScoreForThisAlphabet) {
                        bestFrequencyScoreForThisAlphabet = freqScore;
                        bestKeyForThisAlphabet = key;
                        bestCandidateForThisAlphabet = candidate;
                        bestValidRatioForThisAlphabet = validRatio;
                    }
                }
            }

            // After checking all keys for this extended alphabet, show the best results
            if (candidateMeetingThresholdExistsForThisAlphabet) {
                System.out.println("Likely decryption (segmentation + frequency) for alphabet " + extendedAlphabet + ":");
                System.out.println("    Decryption Key:    " + bestKeyForThisAlphabet);
                System.out.println("    Plaintext:         " + bestCandidateForThisAlphabet);
                System.out.println("    Frequency Score:   " + bestFrequencyScoreForThisAlphabet);
                System.out.println("    Valid Ratio:       " + bestValidRatioForThisAlphabet);
            } else {
                System.out.println("No candidate exceeded the threshold for alphabet " + extendedAlphabet + ".");
                System.out.println("Best candidate by frequency score for this alphabet:");
                System.out.println("    Decryption Key:    " + bestKeyOverallForThisAlphabet);
                System.out.println("    Plaintext:         " + bestCandidateOverallForThisAlphabet);
                System.out.println("    Frequency Score:   " + bestFrequencyScoreOverallForThisAlphabet);
            }

            System.out.println(); // blank line between each block
        }
    }


    public static void main(String[] args) {
        //Aer1vkdyhckdwv
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter ciphertext: ");
        String cipherText = scanner.nextLine().toUpperCase();
        HackShift(cipherText);
    }
}
