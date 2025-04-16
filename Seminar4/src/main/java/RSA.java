import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class RSA {

    /*************************************************************
     * 1) CORE ALGORITHMS
     *************************************************************/

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
        int y = x1 - (a / b) * y1;
        return new int[]{gcd, x, y};
    }

    /**
     * Square-and-multiply (modular exponentiation) to compute (base^exp) mod n efficiently.
     */
    public static int modExp(int base, int exp, int n) {
        int result = 1;
        int current = base % n;
        int e = exp;
        while (e > 0) {
            if ((e & 1) == 1) {
                result = (int)(((long)result * current) % n);
            }
            current = (int)(((long)current * current) % n);
            e >>= 1;
        }
        return result;
    }

    /*************************************************************
     * 2) PADDING METHODS (PKCS-style in Z26)
     *************************************************************/

    /**
     * PKCS-like padding in a Z26 context.
     * If text length is already multiple of blockLength, we add a full extra block of padding.
     * Example: if blockLength=3 and text="HELLO" (5 letters),
     * remainder=2 -> padLen=1 -> append 1 letter. If remainder=0 -> padLen=3.
     *
     * We store 'p' as letter = (char)('A' + (p-1)).
     * e.g. if p=3 -> letter='C' (value=2).
     */
    private static String padPlaintextPKCS(String text, int blockLength) {
        int r = text.length() % blockLength;
        // PKCS7-like: if r==0, pad with full blockLength
        int padLen = (r == 0) ? blockLength : (blockLength - r);

        // We'll assume padLen <= 26 for the block sizes you use in homework
        char padChar = (char)('A' + (padLen - 1));

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
        if (text.length() == 0) {
            return text; // nothing to unpad
        }
        char lastChar = text.charAt(text.length() - 1);
        int padLen = (lastChar - 'A') + 1; // e.g. 'A' -> 1, 'B'->2, ...
        if (padLen <= 0 || padLen > text.length()) {
            // invalid padding
            return text; // or throw an error
        }
        // check all last padLen letters
        for (int i = text.length() - padLen; i < text.length(); i++) {
            if (text.charAt(i) != lastChar) {
                // invalid padding
                return text; // or throw an error
            }
        }
        // remove them
        return text.substring(0, text.length() - padLen);
    }

    /*************************************************************
     * 3) BLOCK ENCODING / DECODING (Z26)
     *************************************************************/

    /**
     * Convert text string into integer blocks based on Z26 encoding.
     * e.g. "ABC" => 0*(26^2) + 1*(26^1) + 2*(26^0) = ...
     */
    private static List<Integer> textToBlocks(String text, int blocklength) {
        text = text.toUpperCase().replaceAll("[^A-Z]", ""); // ensure A-Z only
        List<Integer> blocks = new ArrayList<>();

        int idx = 0;
        while (idx < text.length()) {
            int blockVal = 0;
            for (int i = 0; i < blocklength; i++) {
                blockVal *= 26;
                if (idx < text.length()) {
                    char c = text.charAt(idx);
                    blockVal += (c - 'A');
                }
                idx++;
            }
            blocks.add(blockVal);
        }
        return blocks;
    }

    /**
     * Convert integer blocks back to letters using Z26 with given blocklength.
     */
    private static String blocksToText(List<Integer> blocks, int blocklength) {
        StringBuilder sb = new StringBuilder();
        for (int val : blocks) {
            char[] group = new char[blocklength];
            int tmp = val;
            for (int i = blocklength - 1; i >= 0; i--) {
                int letterVal = tmp % 26;
                tmp /= 26;
                group[i] = (char)('A' + letterVal);
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
     * We compute n = p*q internally.
     * 1. Pad the plaintext
     * 2. Convert to blocks
     * 3. modExp each block
     */
    public static String encrypt(int p, int q, int b, String plaintext, int blocklength) {
        // PKCS-like padding
        String padded = padPlaintextPKCS(plaintext, blocklength);

        int n = p * q;
        List<Integer> blocks = textToBlocks(padded, blocklength);
        List<Integer> encryptedBlocks = new ArrayList<>();
        for (int block : blocks) {
            int cVal = modExp(block, b, n);
            encryptedBlocks.add(cVal);
        }

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
     */
    public static String decrypt(int p, int q, int a, String ciphertext, int blocklength) {
        int n = p * q;

        String[] parts = ciphertext.split(",");
        List<Integer> decryptedBlocks = new ArrayList<>();
        for (String part : parts) {
            int cVal = Integer.parseInt(part.trim());
            int m = modExp(cVal, a, n);
            decryptedBlocks.add(m);
        }
        // Convert blocks -> text
        String rawDecrypted = blocksToText(decryptedBlocks, blocklength);

        // remove PKCS-like padding
        return unpadPlaintextPKCS(rawDecrypted);
    }

    /*************************************************************
     * 5) KEY GENERATION, BLOCKLENGTH, MAIN
     *************************************************************/

    /**
     * Naive primality check (for demonstration only).
     */
    private static boolean isPrime(int x) {
        if (x < 2) return false;
        for (int i = 2; i * i <= x; i++) {
            if (x % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Generates a random prime in a small range for demonstration.
     */
    private static int getRandomPrime(int lowerBound, int upperBound) {
        Random rand = new Random();
        while (true) {
            int candidate = rand.nextInt(upperBound - lowerBound + 1) + lowerBound;
            if (isPrime(candidate)) {
                return candidate;
            }
        }
    }

    /**
     * Simple key-generation scheme for demonstration:
     * Returns array [p, q, b, a].
     */
    public static int[] generateKeyPair() {
        int p = getRandomPrime(100, 200);
        int q = getRandomPrime(200, 300);

        int phi = (p - 1) * (q - 1);
        Random rand = new Random();
        int b;
        // pick a public exponent b that is coprime with phi
        while (true) {
            b = rand.nextInt(phi - 2) + 2;
            int[] euc = extendedEuclid(b, phi);
            if (euc[0] == 1) {
                break;
            }
        }

        // compute the private exponent a
        int[] result = extendedEuclid(b, phi);
        int gcd = result[0];
        int x = result[1];
        if (gcd != 1) {
            throw new RuntimeException("Invalid exponent (gcd != 1).");
        }
        int a = x % phi;
        if (a < 0) {
            a += phi;
        }

        return new int[]{p, q, b, a};
    }

    /**
     * Automatically compute a suitable block length so that 26^blocklength < n.
     * Returns the largest integer blocklength such that 26^blocklength < n.
     */
    private static int autoBlockLength(int p, int q) {
        int n = p * q;
        int blocklength = 1;
        while (true) {
            double testVal = Math.pow(26, blocklength + 1);
            if (testVal < n) {
                blocklength++;
            } else {
                break;
            }
        }
        return blocklength;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Do you want to automatically generate RSA keys? (y/n)");
        String choice = sc.nextLine().trim().toLowerCase();

        int p, q, b, a;
        if (choice.equals("y")) {
            int[] keys = generateKeyPair();
            p = keys[0];
            q = keys[1];
            b = keys[2];
            a = keys[3];
            System.out.println("Generated RSA:");
            System.out.println("  p=" + p + ", q=" + q + ", b=" + b + ", a=" + a);
        } else {
            System.out.println("Enter prime q:");
            q = sc.nextInt();
            System.out.println("Enter prime p:");
            p = sc.nextInt();
            System.out.println("Enter public exponent b:");
            b = sc.nextInt();
            sc.nextLine();

            int phi = (p - 1) * (q - 1);
            int[] euc = extendedEuclid(b, phi);
            if (euc[0] != 1) {
                System.out.println("Error: b and phi(n) are not coprime! Exiting.");
                return;
            }
            int x = euc[1];
            int tempA = x % phi;
            if (tempA < 0) {
                tempA += phi;
            }
            a = tempA;
            System.out.println("Computed private exponent a=" + a);
        }

        System.out.println("Enter plaintext (any non-A-Z characters will be removed):");
        String plaintext = sc.nextLine().toUpperCase().replaceAll("[^A-Z]", "");

        // Auto-compute block length
        int blocklength = autoBlockLength(p, q);
        System.out.println("Automatically computed blocklength: " + blocklength);

        // ENCRYPT
        String ciphertext = encrypt(p, q, b, plaintext, blocklength);
        System.out.println("Ciphertext blocks: " + ciphertext);

        // DECRYPT
        String decrypted = decrypt(p, q, a, ciphertext, blocklength);
        System.out.println("Decrypted Text (after PKCS-like unpad): " + decrypted);

        sc.close();
    }
}
