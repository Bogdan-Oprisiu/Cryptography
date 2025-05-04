import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.lang.Math; // Import Math for pow

public class RSA {

    /**
     * Extended Euclidean Algorithm
     * Finds x, y such that a*x + b*y = gcd(a, b)
     * Returns array [gcd, x, y].
     */
    public static int[] extendedEuclid(int a, int b) {
        if (b == 0) {
            return new int[]{a, 1, 0};
        }
        int[] result = extendedEuclid(b, a % b);
        int gcd = result[0];
        int x1 = result[1];
        int y1 = result[2];

        // Update x and y using results of recursion
        int x = y1;
        // Careful with integer division behavior
        int y = x1 - (a / b) * y1;
        return new int[]{gcd, x, y};
    }

    /**
     * Square-and-multiply (modular exponentiation) to compute (base^exp) mod n efficiently.
     */
    public static int modExp(int base, int exp, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Modulus n must be positive.");
        }
        // Use long for intermediate results to prevent overflow before modulo
        long result = 1;
        long current = base % n;
        // Ensure 'current' is non-negative (handle negative base correctly)
        if (current < 0) {
            current += n;
        }
        int e = exp;

        // Handle edge case where exponent is 0
        if (e == 0) {
            return 1 % n; // (base^0) mod n is 1 (or 0 if n=1, but we check n>0)
        }

        while (e > 0) {
            if ((e & 1) == 1) { // If the last bit of exp is 1
                result = (result * current) % n;
            }
            current = (current * current) % n; // Square the base
            e >>= 1; // Right shift exponent (divide by 2)
        }
        return (int) result;
    }


    /*************************************************************
     * 2) PADDING METHODS (PKCS-style in Z26) - Kept for robustness
     *************************************************************/

    /**
     * PKCS-like padding in a Z26 context.
     * If text length is already multiple of blockLength, we add a full extra block of padding.
     * Example: if blockLength=3 and text="HELLO" (5 letters),
     * remainder=2 -> padLen=1 -> append 1 letter. If remainder=0 -> padLen=3.
     * <p>
     * We store 'p' as letter = (char)('A' + (p-1)).
     * e.g. if p=3 -> letter='C' (value=2).
     */
    private static String padPlaintextPKCS(String text, int blockLength) {
        if (blockLength <= 0) {
            throw new IllegalArgumentException("Block length must be positive for padding.");
        }
        int r = text.length() % blockLength;
        // PKCS7-like: if r==0, pad with full blockLength
        int padLen = (r == 0) ? blockLength : (blockLength - r);

        // Ensure padLen is representable as a letter A-Z (1-26)
        if (padLen > 26) {
            throw new IllegalArgumentException("Cannot represent padding length > 26 with Z26");
        }
        char padChar = (char) ('A' + (padLen - 1));

        StringBuilder sb = new StringBuilder(text);
        for (int i = 0; i < padLen; i++) {
            sb.append(padChar);
        }
        return sb.toString();
    }

    /**
     * Remove PKCS-like padding after decryption.
     * 1. Check last letter => compute p = (letter - 'A') + 1
     * 2. Verify last p letters are all the same pad letter
     * 3. Remove them
     */
    private static String unpadPlaintextPKCS(String text) {
        if (text == null || text.length() == 0) {
            return text; // nothing to unpad
        }
        char lastChar = text.charAt(text.length() - 1);

        // Check if lastChar is within A-Z range
        if (lastChar < 'A' || lastChar > 'Z') {
            System.err.println("Warning: Last character is not A-Z. Assuming no padding or invalid padding.");
            return text;
        }

        int padLen = (lastChar - 'A') + 1; // e.g. 'A' -> 1, 'B'->2, ...

        // Basic validation: pad length must be positive and not exceed text length
        // Also, pad length cannot exceed the block length used (implicitly checked by text length)
        if (padLen <= 0 || padLen > text.length()) {
            System.err.println("Warning: Invalid padding length detected (" + padLen + "). Returning raw text.");
            return text; // or throw an error
        }

        // Check if all last padLen characters match the padding character
        for (int i = text.length() - padLen; i < text.length(); i++) {
            if (text.charAt(i) != lastChar) {
                System.err.println("Warning: Invalid padding sequence detected. Returning raw text.");
                return text; // or throw an error
            }
        }
        // Remove the padding
        return text.substring(0, text.length() - padLen);
    }


    /*************************************************************
     * 3) BLOCK ENCODING / DECODING (Z26)
     *************************************************************/

    /**
     * Convert text string into integer blocks based on Z26 encoding.
     * e.g. "ABC" => 0*(26^2) + 1*(26^1) + 2*(26^0) = 28
     */
    private static List<Integer> textToBlocks(String text, int blocklength) {
        if (blocklength <= 0) {
            throw new IllegalArgumentException("Block length must be positive for encoding.");
        }
        // Ensure text is already padded or its length is a multiple of blocklength
        if (text.length() % blocklength != 0) {
            throw new IllegalArgumentException("Padded text length must be a multiple of blocklength for encoding.");
        }

        text = text.toUpperCase().replaceAll("[^A-Z]", ""); // Ensure A-Z only
        List<Integer> blocks = new ArrayList<>();

        int idx = 0;
        while (idx < text.length()) {
            // Use long for blockVal calculation to prevent overflow with larger block lengths
            long blockVal = 0;
            for (int i = 0; i < blocklength; i++) {
                blockVal *= 26;
                // We assume idx is always < text.length due to the length check above
                char c = text.charAt(idx);
                blockVal += (c - 'A');
                idx++;
            }
            // Check if the calculated block value fits within an integer.
            if (blockVal > Integer.MAX_VALUE) {
                throw new ArithmeticException("Block value " + blockVal + " exceeds integer limits for blocklength " + blocklength);
            }
            blocks.add((int) blockVal);
        }
        return blocks;
    }

    /**
     * Convert integer blocks back to letters using Z26 with given blocklength.
     */
    private static String blocksToText(List<Integer> blocks, int blocklength) {
        if (blocklength <= 0) {
            throw new IllegalArgumentException("Block length must be positive for decoding.");
        }
        StringBuilder sb = new StringBuilder();
        for (int val : blocks) {
            // Handle potential negative values (should not happen with correct RSA decryption)
            int currentVal = val;
            if (currentVal < 0) {
                System.err.println("Warning: Negative block value encountered: " + val + ". Skipping block.");
                continue;
            }

            char[] group = new char[blocklength];
            long temp = currentVal; // Use long for intermediate division/modulo
            boolean valueTooLarge = false;

            for (int i = blocklength - 1; i >= 0; i--) {
                int letterVal = (int) (temp % 26);
                temp /= 26;
                group[i] = (char) ('A' + letterVal);
            }
            // Check if after decoding all digits, temp is zero. If not, the original
            // number was too large for this blocklength (implies block value >= 26^blocklength).
            // This shouldn't happen if block value < n and n > 26^blocklength, but check defensively.
            if (temp != 0) {
                System.err.println("Warning: Block value " + val + " seems too large for blocklength " + blocklength + ". Skipping block.");
                continue; // Skip this block as it cannot be represented correctly
            }
            sb.append(group);
        }
        return sb.toString();
    }


    /*************************************************************
     * 4) RSA ENCRYPT / DECRYPT
     *************************************************************/

    /**
     * RSA Encryption (with PKCS-like padding).
     * Input p, q: prime factors of n
     * Input b: public exponent
     * Input plaintext: original message string
     * Input blocklength: number of letters per block
     * Returns: comma-separated string of encrypted block integers
     */
    public static String encrypt(int p, int q, int b, String plaintext, int blocklength) {
        int n = p * q;
        // Pre-validate blocklength against n before proceeding
        if (!isValidBlockLength(blocklength, n)) {
            throw new IllegalArgumentException("Block length " + blocklength + " is invalid for n=" + n);
        }

        // PKCS-like padding
        String padded = padPlaintextPKCS(plaintext, blocklength);
        System.out.println("Padded text: " + padded); // Debugging: show padded text


        List<Integer> blocks = textToBlocks(padded, blocklength);
        List<Integer> encryptedBlocks = new ArrayList<>();

        System.out.print("Encrypting blocks: "); // Debugging
        for (int block : blocks) {
            // Block value must be < n (already implicitly checked by textToBlocks
            // if isValidBlockLength check passed, but double-checking doesn't hurt)
            if (block >= n) {
                throw new IllegalArgumentException("Block value " + block + " is >= n (" + n + "). This should not happen with valid block length.");
            }
            System.out.print(block + " -> "); // Debugging
            int cVal = modExp(block, b, n);
            System.out.print(cVal + "; "); // Debugging
            encryptedBlocks.add(cVal);
        }
        System.out.println(); // Newline after debugging output

        // Format output as comma-separated string
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < encryptedBlocks.size(); i++) {
            sb.append(encryptedBlocks.get(i));
            if (i < encryptedBlocks.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * RSA Decryption (with PKCS-like unpadding).
     * Input p, q: prime factors of n
     * Input a: private exponent
     * Input ciphertext: comma-separated string of encrypted block integers
     * Input blocklength: number of letters per block used during encryption
     * Returns: original plaintext string
     */
    public static String decrypt(int p, int q, int a, String ciphertext, int blocklength) {
        int n = p * q;
        // Pre-validate blocklength (needed for blocksToText)
        if (blocklength <= 0) {
            throw new IllegalArgumentException("Block length must be positive for decryption.");
        }

        // Input validation for ciphertext format
        if (ciphertext == null || ciphertext.trim().isEmpty()) {
            System.err.println("Warning: Empty ciphertext provided.");
            return "";
        }
        String[] parts = ciphertext.split(",");
        List<Integer> decryptedBlocks = new ArrayList<>();

        System.out.print("Decrypting blocks: "); // Debugging
        for (String part : parts) {
            int cVal;
            try {
                cVal = Integer.parseInt(part.trim());
                // Ciphertext value must be within [0, n-1]
                if (cVal < 0 || cVal >= n) {
                    System.err.println("Warning: Ciphertext value " + cVal + " is out of expected range [0, " + (n-1) + "]. Skipping block.");
                    continue; // Skip this invalid block
                }
            } catch (NumberFormatException e) {
                System.err.println("Warning: Invalid number format in ciphertext: '" + part + "'. Skipping block.");
                continue; // Skip this invalid part
            }

            System.out.print(cVal + " -> "); // Debugging
            int m = modExp(cVal, a, n);
            System.out.print(m + "; "); // Debugging
            decryptedBlocks.add(m);
        }
        System.out.println(); // Newline after debugging output

        // Convert blocks -> text
        String rawDecrypted = blocksToText(decryptedBlocks, blocklength);
        System.out.println("Raw decrypted text (before unpadding): " + rawDecrypted); // Debugging

        // Remove PKCS-like padding
        return unpadPlaintextPKCS(rawDecrypted);
    }


    /*************************************************************
     * 5) KEY GENERATION, HELPER, MAIN
     *************************************************************/

    /**
     * Checks if the chosen blocklength is valid for RSA (26^blocklength < n).
     * Uses double for calculation to handle potentially large powers.
     */
    private static boolean isValidBlockLength(int blocklength, int n) {
        if (blocklength <= 0) {
            return false;
        }
        // Use double for the power calculation to avoid potential overflow
        // with integer types if blocklength is large, even if the result
        // is expected to be less than n (which might be close to Integer.MAX_VALUE).
        double maxBlockValue = Math.pow(26, blocklength);

        // Check 1: The maximum value representable by the block must be less than n.
        // Use a small tolerance for double comparison if necessary, but direct < should work.
        boolean condition1 = maxBlockValue < n;

        // Check 2: The maximum value must not exceed what an int can hold.
        boolean condition2 = maxBlockValue <= Integer.MAX_VALUE;

        return condition1 && condition2;
    }

    /**
     * Naive primality check (sufficient for small primes).
     */
    private static boolean isPrime(int x) {
        if (x < 2) return false;
        if (x == 2 || x == 3) return true;
        if (x % 2 == 0 || x % 3 == 0) return false;
        // Check factors up to sqrt(x)
        for (int i = 5; i * i <= x; i = i + 6) {
            if (x % i == 0 || x % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Generates a random prime in a specified range for demonstration.
     * Tries multiple times; throws exception if no prime found quickly.
     */
    private static int getRandomPrime(int lowerBound, int upperBound) {
        Random rand = new Random();
        int attempts = 0;
        int maxAttempts = 1000; // Limit attempts to avoid infinite loop if range is bad

        while (attempts < maxAttempts) {
            // Ensure range is valid
            if (lowerBound > upperBound) {
                throw new IllegalArgumentException("Lower bound cannot be greater than upper bound.");
            }
            if (lowerBound < 2) lowerBound = 2; // Primes must be >= 2

            int candidate = rand.nextInt(upperBound - lowerBound + 1) + lowerBound;
            if (isPrime(candidate)) {
                return candidate;
            }
            attempts++;
        }
        throw new RuntimeException("Could not find a prime in the range [" + lowerBound + ", " + upperBound + "] after " + maxAttempts + " attempts.");
    }

    /**
     * Simple key-generation scheme for demonstration:
     * Returns array [p, q, b, a].
     */
    public static int[] generateKeyPair() {
        // Define ranges for p and q
        // Ensure ranges allow for n > 26^k for reasonable k (e.g., k=2 -> n>676, k=3 -> n>17576)
        int p_min = 100, p_max = 300;
        int q_min = 301, q_max = 500; // Ensure q's range doesn't overlap with p's

        int p = getRandomPrime(p_min, p_max);
        int q = getRandomPrime(q_min, q_max);
        // Ensure p and q are distinct (already guaranteed by non-overlapping ranges, but good practice)
        // while (p == q) {
        //     q = getRandomPrime(q_min, q_max);
        // }

        int n = p * q;
        // Use long for phi calculation to avoid overflow if p, q are large
        long phi_long = (long)(p - 1) * (long)(q - 1);

        // Ensure phi fits in an int if we need it as int later
        if (phi_long > Integer.MAX_VALUE) {
            throw new ArithmeticException("phi(n) exceeds integer limits.");
        }
        int phi = (int) phi_long;


        Random rand = new Random();
        int b;
        int maxExponentAttempts = 1000;
        int exponentAttempts = 0;

        // Pick a public exponent b such that 1 < b < phi and gcd(b, phi) = 1
        while (true) {
            if (exponentAttempts++ > maxExponentAttempts) {
                throw new RuntimeException("Failed to find a suitable public exponent b after " + maxExponentAttempts + " attempts.");
            }
            // Generate b in the range [3, phi-1], often odd numbers are chosen
            // Ensure phi > 2 to have a valid range
            if (phi <= 2) {
                throw new RuntimeException("phi(n) is too small to find a suitable public exponent.");
            }

            b = rand.nextInt(phi - 3) + 3; // Range [3, phi-1]

            // Check gcd(b, phi) == 1 using extendedEuclid
            int[] euc = extendedEuclid(b, phi);
            if (euc[0] == 1) {
                break; // Found a suitable b
            }
        }

        // Compute the private exponent a using the result from extendedEuclid(b, phi)
        int[] result = extendedEuclid(b, phi); // We know gcd is 1
        int x = result[1]; // x is the modular inverse of b mod phi

        // Ensure 'a' is the smallest positive inverse: a = x mod phi
        int a = x % phi;
        if (a < 0) {
            a += phi;
        }

        // Final validation: Check (b * a) mod phi == 1
        // Use long for multiplication to prevent overflow
        if (((long) b * a) % phi != 1) {
            throw new RuntimeException("Key generation error: (b*a) % phi != 1. b=" + b + ", a=" + a + ", phi=" + phi);
        }

        return new int[]{p, q, b, a};
    }


    // --- autoBlockLength is not used directly in main ---


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("RSA Encryption/Decryption (Z26)");
        System.out.println("--------------------------------");

        System.out.println("Do you want to automatically generate RSA keys? (y/n)");
        String choice = "";
        // Loop until valid choice is entered
        while (!choice.equals("y") && !choice.equals("n")) {
            choice = sc.nextLine().trim().toLowerCase();
            if (!choice.equals("y") && !choice.equals("n")) {
                System.out.println("Please enter 'y' or 'n'.");
            }
        }


        int p = 0, q = 0, b = 0, a = 0;
        int n = 0; // Calculate n once
        boolean keysValid = false;

        if (choice.equals("y")) {
            try {
                int[] keys = generateKeyPair();
                p = keys[0];
                q = keys[1];
                b = keys[2];
                a = keys[3];
                n = p * q;
                keysValid = true; // Assume generation is valid if no exception
                System.out.println("Generated RSA Keys:");
                System.out.println("  p = " + p + ", q = " + q);
                System.out.println("  n = p * q = " + n);
                System.out.println("  phi(n) = " + (long)(p - 1) * (long)(q - 1));
                System.out.println("  Public exponent b = " + b);
                System.out.println("  Private exponent a = " + a);
            } catch (Exception e) {
                System.err.println("Error during automatic key generation: " + e.getMessage());
                keysValid = false;
            }

        } else {
            // Manual key input
            boolean inputSuccessful = false;
            while (!inputSuccessful) {
                try {
                    System.out.println("Enter prime p:");
                    p = sc.nextInt();
                    System.out.println("Enter prime q:");
                    q = sc.nextInt();
                    sc.nextLine(); // Consume newline after int input

                    // Validate p and q
                    if (!isPrime(p)) {
                        System.out.println("Error: p (" + p + ") is not prime.");
                        continue; // Ask again
                    }
                    if (!isPrime(q)) {
                        System.out.println("Error: q (" + q + ") is not prime.");
                        continue; // Ask again
                    }
                    if (p == q) {
                        System.out.println("Error: p and q cannot be the same prime.");
                        continue; // Ask again
                    }

                    n = p * q;
                    System.out.println("Calculated n = p * q = " + n);

                    // Use long for phi calculation
                    long phi_long = (long)(p - 1) * (long)(q - 1);
                    if (phi_long > Integer.MAX_VALUE) {
                        System.out.println("Error: phi(n) is too large for standard integer operations.");
                        continue; // Cannot proceed safely
                    }
                    int phi = (int) phi_long;
                    System.out.println("Calculated phi(n) = (p-1)*(q-1) = " + phi);

                    System.out.println("Enter public exponent b (must be coprime to " + phi + "):");
                    b = sc.nextInt();
                    sc.nextLine(); // Consume newline

                    // Validate b
                    if (b <= 1 || b >= phi) {
                        System.out.println("Error: Public exponent b must be between 1 and phi(n) (exclusive).");
                        continue;
                    }

                    // Check gcd(b, phi) == 1
                    int[] euc = extendedEuclid(b, phi);
                    if (euc[0] != 1) {
                        System.out.println("Error: b (" + b + ") and phi(n) (" + phi + ") are not coprime (gcd=" + euc[0] + ").");
                        continue;
                    }

                    // Compute private exponent a
                    int x = euc[1];
                    int tempA = x % phi;
                    if (tempA < 0) {
                        tempA += phi;
                    }
                    a = tempA;
                    System.out.println("Computed private exponent a = " + a);

                    // Verify b*a mod phi == 1
                    if (((long) b * a) % phi != 1) {
                        // This should theoretically not happen if gcd is 1 and 'a' computation is correct
                        System.out.println("Error: Key validation failed [(b*a) % phi != 1]. There might be an issue with extendedEuclid or 'a' calculation.");
                        continue;
                    }

                    inputSuccessful = true; // All checks passed
                    keysValid = true;

                } catch (java.util.InputMismatchException e) {
                    System.out.println("Invalid input. Please enter integers for p, q, and b.");
                    sc.nextLine(); // Consume the rest of the invalid line
                } catch (Exception e) {
                    System.err.println("An unexpected error occurred during input: " + e.getMessage());
                    // Optionally break or exit here depending on desired robustness
                }
            } // End while (!inputSuccessful)

        } // End else (manual input)


        // --- Proceed only if keys are valid ---
        if (keysValid) {
            int blocklength = 0;
            boolean blockLengthValid = false;

            while (!blockLengthValid) {
                System.out.println("Enter block length (integer > 0):");
                // Check if the next input is an integer
                if (!sc.hasNextInt()) {
                    System.out.println("Invalid input. Please enter an integer for block length.");
                    sc.nextLine(); // <-- CORRECTED: Consume the entire invalid line
                    continue;      // Go back to the start of the loop
                }

                // If it is an integer, read it
                blocklength = sc.nextInt();
                sc.nextLine(); // Consume the newline character left after reading the integer

                // Now validate the integer value using the helper function
                if (isValidBlockLength(blocklength, n)) {
                    System.out.println("Using blocklength: " + blocklength);
                    blockLengthValid = true; // Exit loop
                } else {
                    // Provide specific feedback based on why it failed
                    if (blocklength <= 0) {
                        System.out.println("Error: Block length must be a positive integer.");
                    } else {
                        double maxBlockValue = Math.pow(26, blocklength);
                        // Check against n using double comparison first
                        if (maxBlockValue >= n) {
                            // Use String.format for potentially large doubles if needed
                            System.out.println(String.format("Error: Invalid block length. 26^%d (approx %.0f) must be strictly less than n (%d).", blocklength, maxBlockValue, n));
                        } else { // If < n, it must have exceeded Integer.MAX_VALUE if isValidBlockLength failed
                            System.out.println(String.format("Error: Invalid block length. 26^%d (approx %.0f) exceeds maximum integer value (%d).", blocklength, maxBlockValue, Integer.MAX_VALUE));
                        }
                    }
                    // Loop will repeat, asking for input again
                }
            } // End while (!blockLengthValid)


            System.out.println("Enter plaintext (A-Z only, others removed):");
            String plaintext = sc.nextLine().toUpperCase().replaceAll("[^A-Z]", "");
            if (plaintext.isEmpty()) {
                System.out.println("Warning: Plaintext is empty after filtering.");
                // Decide if encryption should proceed or exit
                // Exiting for now, as encrypting empty string might be ambiguous
                System.out.println("Exiting.");
            } else {
                System.out.println("Processing plaintext: " + plaintext);

                // ENCRYPT and DECRYPT within try-catch for potential runtime errors
                try {
                    String ciphertext = encrypt(p, q, b, plaintext, blocklength);
                    System.out.println("Ciphertext blocks: " + ciphertext);

                    // DECRYPT
                    String decrypted = decrypt(p, q, a, ciphertext, blocklength);
                    System.out.println("Decrypted Text (after unpad): " + decrypted);

                    // Verification step
                    System.out.println("--- Verification ---");
                    if (plaintext.equals(decrypted)) {
                        System.out.println("SUCCESS: Decrypted text matches original plaintext.");
                    } else {
                        System.out.println("FAILURE: Decrypted text does NOT match original plaintext!");
                        System.out.println("Original:  '" + plaintext + "'");
                        System.out.println("Decrypted: '" + decrypted + "'");
                    }

                } catch (IllegalArgumentException | ArithmeticException e) {
                    System.err.println("An error occurred during encryption/decryption: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("An unexpected error occurred: " + e.getMessage());
                    e.printStackTrace(); // Print stack trace for debugging unexpected errors
                }
            } // End else (plaintext not empty)

        } else {
            System.out.println("Cannot proceed without valid RSA keys.");
        }


        sc.close();
        System.out.println("Program finished.");
    } // End main
}