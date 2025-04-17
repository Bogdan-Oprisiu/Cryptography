import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * RSA_hack:
 * Demonstrates how to factor a small n, find the private exponent a,
 * then decrypt the known ciphertext blocks using standard RSA steps.
 */
public class RSA_hack {

    /**
     * Extended Euclidean Algorithm
     * Returns [gcd, x, y] such that a*x + b*y = gcd(a, b)
     */
    private static int[] extendedEuclid(int a, int b) {
        if (b == 0) return new int[]{a, 1, 0};

        int[] result = extendedEuclid(b, a % b);
        int gcd = result[0];
        int x1 = result[1];
        int y1 = result[2];

        // update x, y
        int y = x1 - (a / b) * y1;
        return new int[]{gcd, y1, y};
    }

    /**
     * Naive method to factor a small n by trial division
     * Returns array [p, q] with p <= q if successful
     */
    private static int[] factorN(int n) {
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                int p = i;
                int q = n / i;
                return new int[]{p, q};
            }
        }
        // If we get here, we failed (n might be prime or too large for naive factoring)
        throw new RuntimeException("Failed to factor n=" + n + " with naive approach.");
    }

    /**
     * Square-and-multiply to compute (base^exp) mod n efficiently
     */
    private static int modExp(int base, int exp, int n) {
        long result = 1;
        long cur = base % n;
        int e = exp;
        while (e > 0) {
            if ((e & 1) == 1) {
                result = (result * cur) % n;
            }
            cur = (cur * cur) % n;
            e >>= 1;
        }
        return (int) result;
    }

    /**
     * Decodes a single decrypted integer block (0 <= block < 26^3)
     * into its corresponding letter(s) in base-26.
     */
    private static String decodeBlock(int block) {
        if (block == 0) {
            // The block is all zeros -> 'A'
            return "A";
        } else if (block < 26) {
            // 1-letter block
            return String.valueOf((char) (block + 'A'));
        } else if (block < 26 * 26) {
            // 2-letter block
            int first = block / 26;      // top digit
            int second = block % 26;     // bottom digit
            return "" + (char) (first + 'A') + (char) (second + 'A');
        } else {
            // 3-letter block
            int first = block / (26 * 26);
            int remainder = block % (26 * 26);
            int second = remainder / 26;
            int third = remainder % 26;

            return "" + (char) (first + 'A')
                    + (char) (second + 'A')
                    + (char) (third + 'A');
        }
    }

    /**
     * Decrypt each block C using M = C^a mod n.
     * Then decode M as up to 3 letters in base-26 (skipping leading zeros).
     */
    public static String hackRSA(int n, int b, String ciphertext) {
        // 1) Factor n to find p, q
        int[] pq = factorN(n);
        int p = pq[0];
        int q = pq[1];

        // 2) Compute phi(n) = (p - 1) * (q - 1)
        int phi = (p - 1) * (q - 1);

        // 3) Compute private exponent a via extended Euclid
        int[] euclidResult = extendedEuclid(b, phi);
        if (euclidResult[0] != 1) {
            throw new RuntimeException("b and phi(n) are not coprime. Hack fails.");
        }
        int x = euclidResult[1];
        int a = x % phi;
        if (a < 0) {
            a += phi;
        }

        // 4) Decrypt each ciphertext block -> M
        String[] blocks = ciphertext.split(",");
        List<Integer> decryptedBlocks = new ArrayList<>();
        for (String blk : blocks) {
            int cVal = Integer.parseInt(blk.trim());
            int m = modExp(cVal, a, n);
            decryptedBlocks.add(m);
        }

        // 5) Decode each block into letters
        StringBuilder plaintextBuilder = new StringBuilder();
        for (int block : decryptedBlocks) {
            plaintextBuilder.append(decodeBlock(block));
        }

        // Return the final plaintext without globally stripping trailing 'A's
        return plaintextBuilder.toString();
    }

    /**
     * Simple main that can read n, b, ciphertext from user,
     * call hackRSA, and show the plaintext result.
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Example usage:
        int n = 18721;
        int b = 25;
        String ciphertext = "365,0,4845,14930,2608,2608,0";

        String hackedPlaintext = hackRSA(n, b, ciphertext);
        System.out.println("Recovered Plaintext (raw Z26 decode): " + hackedPlaintext);

        sc.close();
    }
}
