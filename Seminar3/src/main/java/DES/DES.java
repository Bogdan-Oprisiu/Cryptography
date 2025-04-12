package DES;

public class DES {

    // ------------------ Tables for DES ------------------
    private static final int[] IP = {
            58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7
    };

    private static final int[] FP = {
            40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9, 49, 17, 57, 25
    };

    private static final int[] E = {
            32, 1, 2, 3, 4, 5,
            4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32, 1
    };

    private static final int[] P = {
            16, 7, 20, 21,
            29, 12, 28, 17,
            1, 15, 23, 26,
            5, 18, 31, 10,
            2, 8, 24, 14,
            32, 27, 3, 9,
            19, 13, 30, 6,
            22, 11, 4, 25
    };

    private static final int[] PC1 = {
            57, 49, 41, 33, 25, 17, 9,
            1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27,
            19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15,
            7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29,
            21, 13, 5, 28, 20, 12, 4
    };

    private static final int[] PC2 = {
            14, 17, 11, 24, 1, 5,
            3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8,
            16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53,
            46, 42, 50, 36, 29, 32
    };

    private static final int[] SHIFTS = {
            1, 1, 2, 2, 2, 2, 2, 2,
            1, 2, 2, 2, 2, 2, 2, 1
    };

    private static final int[][][] S_BOX = {
            {
                    {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
                    {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
                    {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
                    {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}
            },
            {
                    {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
                    {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
                    {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
                    {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}
            },
            {
                    {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
                    {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
                    {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
                    {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}
            },
            {
                    {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
                    {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
                    {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
                    {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}
            },
            {
                    {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
                    {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
                    {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
                    {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}
            },
            {
                    {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
                    {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
                    {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
                    {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}
            },
            {
                    {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
                    {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
                    {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
                    {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}
            },
            {
                    {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
                    {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
                    {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
                    {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}
            }
    };

    // -------------- Utility Methods --------------
    public static int[] hexStringToBitArray(String hex, int bitLength) {
        int[] bits = new int[bitLength];
        StringBuilder binStr = new StringBuilder();
        for (int i = 0; i < hex.length(); i++) {
            int val = Integer.parseInt(hex.substring(i, i + 1), 16);
            binStr.append(String.format("%4s", Integer.toBinaryString(val)).replace(' ', '0'));
        }
        while (binStr.length() < bitLength) binStr.insert(0, "0");
        for (int i = 0; i < bitLength; i++) bits[i] = binStr.charAt(i) - '0';
        return bits;
    }

    public static String bitArrayToHexString(int[] bits) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bits.length; i += 4) {
            int nibble = (bits[i] << 3) | (bits[i + 1] << 2) | (bits[i + 2] << 1) | bits[i + 3];
            sb.append(Integer.toHexString(nibble));
        }
        return sb.toString();
    }

    public static String bitArrayToBinaryString(int[] bits) {
        StringBuilder bin = new StringBuilder();
        for (int b : bits) bin.append(b);
        return bin.toString();
    }

    public static int[] permute(int[] input, int[] table) {
        int[] out = new int[table.length];
        for (int i = 0; i < table.length; i++) out[i] = input[table[i] - 1];
        return out;
    }

    public static int[] leftShift(int[] bits, int n) {
        int[] shifted = new int[bits.length];
        for (int i = 0; i < bits.length; i++) {
            shifted[i] = bits[(i + n) % bits.length];
        }
        return shifted;
    }

    public static int[] xor(int[] a, int[] b) {
        int[] out = new int[a.length];
        for (int i = 0; i < a.length; i++) out[i] = a[i] ^ b[i];
        return out;
    }

    public static int[] concat(int[] left, int[] right) {
        int[] out = new int[left.length + right.length];
        System.arraycopy(left, 0, out, 0, left.length);
        System.arraycopy(right, 0, out, left.length, right.length);
        return out;
    }

    // -------------- Key Scheduling & Round Function --------------
    public static int[][] generateRoundKeys(String keyHex) {
        int[] keyBits = hexStringToBitArray(keyHex, 64);
        int[] key56 = permute(keyBits, PC1);

        int[] C = new int[28], D = new int[28];
        System.arraycopy(key56, 0, C, 0, 28);
        System.arraycopy(key56, 28, D, 0, 28);

        int[][] roundKeys = new int[16][48];
        for (int i = 0; i < 16; i++) {
            C = leftShift(C, SHIFTS[i]);
            D = leftShift(D, SHIFTS[i]);
            int[] CD = concat(C, D);
            roundKeys[i] = permute(CD, PC2);
        }
        return roundKeys;
    }

    public static int[] fFunction(int[] R, int[] roundKey) {
        int[] expanded = permute(R, E);
        int[] xored = xor(expanded, roundKey);
        int[] sboxOut = new int[32];

        for (int i = 0; i < 8; i++) {
            int row = (xored[i * 6] << 1) | xored[i * 6 + 5];
            int col = (xored[i * 6 + 1] << 3) | (xored[i * 6 + 2] << 2)
                    | (xored[i * 6 + 3] << 1) | xored[i * 6 + 4];
            int val = S_BOX[i][row][col];
            for (int j = 0; j < 4; j++) {
                sboxOut[i * 4 + (3 - j)] = (val >> j) & 1;
            }
        }
        return permute(sboxOut, P);
    }

    // -------------- Single-Block Encryption & Decryption --------------
    public static String encryptBlock(String plainHex, String keyHex) {
        int[] ptBits = permute(hexStringToBitArray(plainHex, 64), IP);
        int[] L = new int[32], R = new int[32];
        System.arraycopy(ptBits, 0, L, 0, 32);
        System.arraycopy(ptBits, 32, R, 0, 32);

        int[][] roundKeys = generateRoundKeys(keyHex);

        // Print round-by-round
        for (int i = 0; i < 16; i++) {
            int[] oldR = R;
            int[] fOut = fFunction(R, roundKeys[i]);
            R = xor(L, fOut);
            L = oldR;
            int[] roundConcat = concat(R, L);
            System.out.println("Round " + (i + 1) + ":");
            System.out.println("R[i]L[i]: " + bitArrayToBinaryString(roundConcat));
        }

        int[] preoutput = concat(R, L);
        int[] ctBits = permute(preoutput, FP);

        String ctHex = bitArrayToHexString(ctBits);
        System.out.println("Final Ciphertext (hex): " + ctHex);
        return ctHex;
    }

    // Decrypt a single 64-bit block (16 hex digits) using DES.
// We perform the same round loop as encryption but use the round keys in reverse order.
    public static String decryptBlock(String cipherHex, String keyHex) {
        int[] ctBits = permute(hexStringToBitArray(cipherHex, 64), IP);
        int[] L = new int[32], R = new int[32];
        System.arraycopy(ctBits, 0, L, 0, 32);
        System.arraycopy(ctBits, 32, R, 0, 32);
        int[][] roundKeys = generateRoundKeys(keyHex);
        // Instead of iterating in reverse, we run the same loop as encryption while reversing the key order.
        for (int i = 0; i < 16; i++) {
            int[] oldR = R;
            R = xor(L, fFunction(R, roundKeys[15 - i]));  // Use the keys in reverse order.
            L = oldR;
        }
        int[] preoutput = concat(R, L);
        int[] ptBits = permute(preoutput, FP);
        return bitArrayToHexString(ptBits);
    }

    // For multi-block decryption, use your existing method:
    public static String decryptText(String ciphertextHex, String keyHex) {
        StringBuilder plainHex = new StringBuilder();
        for (int i = 0; i < ciphertextHex.length(); i += 16) {
            String block = ciphertextHex.substring(i, i + 16);
            plainHex.append(decryptBlock(block, keyHex));
        }
        return hexToString(unpadHex(plainHex.toString()));
    }


    // -------------- Text <-> Hex, Padding, Multi-Block --------------
    public static String textToHex(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            sb.append(String.format("%02x", (int) c));
        }
        return sb.toString();
    }

    // Zero-nibble pad to multiple of 16 hex digits
    public static String padHex(String hex) {
        int remainder = hex.length() % 16;
        if (remainder != 0) {
            int missing = 16 - remainder;
            for (int i = 0; i < missing; i++) {
                hex += "0";
            }
        }
        return hex;
    }

    public static String unpadHex(String hex) {
        int idx = hex.length() - 1;
        while (idx >= 0 && hex.charAt(idx) == '0') {
            idx--;
        }
        return (idx < 0) ? "" : hex.substring(0, idx + 1);
    }

    public static String hexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String twoDigits = hex.substring(i, Math.min(i + 2, hex.length()));
            int val = Integer.parseInt(twoDigits, 16);
            sb.append((char) val);
        }
        return sb.toString();
    }

    public static String encryptText(String text, String keyHex) {
        String asciiHex = padHex(textToHex(text));
        System.out.println("[INFO] Text as hex: " + asciiHex);

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < asciiHex.length(); i += 16) {
            String block = asciiHex.substring(i, i + 16);
            System.out.println("\n--- Encrypting block: " + block + " ---");
            out.append(encryptBlock(block, keyHex));
        }
        return out.toString();
    }

    // -------------- MAIN --------------
    public static void main(String[] args) {
        // Single-block example from your professor
        String key = "3b3898371520f75e";
        String block = "8f03456d3f78e2c5";

        System.out.println("=== Single-Block Encryption (Professor's Example) ===");
        System.out.println("Key:       " + key);
        System.out.println("Datablock: " + block);
        System.out.println("==========================================");
        String cipherHex = encryptBlock(block, key);
        System.out.println("Computed Final Ciphertext: " + cipherHex + " (expected: fc914f586f29d5f5)");

        // Single-block decryption of professor's example
        System.out.println("\n=== Single-Block Decryption (Professor's Example) ===");
        String recoveredBlock = decryptBlock(cipherHex, key);
        System.out.println("Recovered Plaintext Block: " + recoveredBlock + " (expected: " + block + ")");

        // Multi-block encryption
        String text = "DES algoritm is better than classical algorithms";
        System.out.println("\n=== Multi-Block Encryption of a Text ===");
        System.out.println("Plaintext: " + text);
        System.out.println("------------------------------------------");
        String textCipher = encryptText(text, key);
        System.out.println("\n[FINAL] Encrypted text (hex): " + textCipher);

        // Multi-block decryption
        System.out.println("\n=== Multi-Block Decryption ===");
        String recoveredText = decryptText(textCipher, key);
        System.out.println("Decrypted Text: " + recoveredText);
    }
}
