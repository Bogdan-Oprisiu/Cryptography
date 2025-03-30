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

    // Fixed alphabet: English letters plus digits 0-9.
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

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
     * It tries every possible key and uses both the frequency score and the ratio of valid segmented words.
     */
    public static void HackShift(String cipherText) {
        // Initialize the spell checker. Ensure the dictionary file (e.g., english.0)
        // is placed in src/main/resources.
        SpellCheckerUtil spellCheckerUtil = new SpellCheckerUtil("src/main/resources/english.0");

        int bestKey = -1;               // Best key among candidates meeting valid ratio threshold.
        double bestFrequencyScore = -1; // Best frequency score among candidates meeting threshold.
        double bestValidRatio = -1;
        String bestCandidate = "";

        int bestKeyOverall = -1;        // Best key overall (by frequency score)
        double bestFrequencyScoreOverall = -1;
        String bestCandidateOverall = "";

        boolean candidateMeetingThresholdExists = false;

        // Try each possible key.
        for (int key = 0; key < ALPHABET.length(); key++) {
            String candidate = decryptUsingKey(cipherText, key, ALPHABET);
            double freqScore = calculateScore(candidate);

            // Use the segmentation method to split text into words.
            String[] segmentedWords = segmentText(candidate, spellCheckerUtil);
            double validRatio = 0.0;
            if (segmentedWords.length > 0) {
                int validCount = 0;
                for (String word : segmentedWords) {
                    // Count word as valid if it is all digits or if the spell checker confirms it's a valid English word.
                    if (!word.isEmpty() && (word.matches("\\d+") || spellCheckerUtil.isEnglishWord(word))) {
                        validCount++;
                    }
                }
                validRatio = (double) validCount / segmentedWords.length;
            }

            // Update best overall candidate based on frequency score.
            if (freqScore > bestFrequencyScoreOverall) {
                bestFrequencyScoreOverall = freqScore;
                bestKeyOverall = key;
                bestCandidateOverall = candidate;
            }

            // If candidate meets the valid ratio threshold, update the best candidate among these.
            if (validRatio >= VALID_RATIO_THRESHOLD) {
                candidateMeetingThresholdExists = true;
                if (freqScore > bestFrequencyScore) {
                    bestFrequencyScore = freqScore;
                    bestKey = key;
                    bestCandidate = candidate;
                    bestValidRatio = validRatio;
                }
            }

//            // Debug output: print candidate info.
//            System.out.println("Key " + key + ": " + candidate
//                    + " | Frequency Score: " + freqScore
//                    + " | Valid Ratio: " + validRatio);
        }

//        System.out.println();
        if (candidateMeetingThresholdExists) {
            System.out.println("Likely decryption based on segmentation and frequency score:");
            System.out.println("Decryption Key: " + bestKey);
            System.out.println("Plaintext: " + bestCandidate);
            System.out.println("Frequency Score: " + bestFrequencyScore + " | Valid Ratio: " + bestValidRatio);
        } else {
            System.out.println("No candidate exceeded the spell checker threshold.");
            System.out.println("Best candidate by frequency score:");
            System.out.println("Decryption Key: " + bestKeyOverall);
            System.out.println("Plaintext: " + bestCandidateOverall);
            System.out.println("Frequency Score: " + bestFrequencyScoreOverall);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter ciphertext: ");
        String cipherText = scanner.nextLine().toUpperCase();
        HackShift(cipherText);
    }
}
