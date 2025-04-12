package AES;

public class AES128 {

    // ------------------------ Standard AES S-Box ------------------------
    private static final int[] SBOX = {
            0x63,0x7c,0x77,0x7b, 0xf2,0x6b,0x6f,0xc5,
            0x30,0x01,0x67,0x2b, 0xfe,0xd7,0xab,0x76,
            0xca,0x82,0xc9,0x7d, 0xfa,0x59,0x47,0xf0,
            0xad,0xd4,0xa2,0xaf, 0x9c,0xa4,0x72,0xc0,
            0xb7,0xfd,0x93,0x26, 0x36,0x3f,0xf7,0xcc,
            0x34,0xa5,0xe5,0xf1, 0x71,0xd8,0x31,0x15,
            0x04,0xc7,0x23,0xc3, 0x18,0x96,0x05,0x9a,
            0x07,0x12,0x80,0xe2, 0xeb,0x27,0xb2,0x75,
            0x09,0x83,0x2c,0x1a, 0x1b,0x6e,0x5a,0xa0,
            0x52,0x3b,0xd6,0xb3, 0x29,0xe3,0x2f,0x84,
            0x53,0xd1,0x00,0xed, 0x20,0xfc,0xb1,0x5b,
            0x6a,0xcb,0xbe,0x39, 0x4a,0x4c,0x58,0xcf,
            0xd0,0xef,0xaa,0xfb, 0x43,0x4d,0x33,0x85,
            0x45,0xf9,0x02,0x7f, 0x50,0x3c,0x9f,0xa8,
            0x51,0xa3,0x40,0x8f, 0x92,0x9d,0x38,0xf5,
            0xbc,0xb6,0xda,0x21, 0x10,0xff,0xf3,0xd2,
            0xcd,0x0c,0x13,0xec, 0x5f,0x97,0x44,0x17,
            0xc4,0xa7,0x7e,0x3d, 0x64,0x5d,0x19,0x73,
            0x60,0x81,0x4f,0xdc, 0x22,0x2a,0x90,0x88,
            0x46,0xee,0xb8,0x14, 0xde,0x5e,0x0b,0xdb,
            0xe0,0x32,0x3a,0x0a, 0x49,0x06,0x24,0x5c,
            0xc2,0xd3,0xac,0x62, 0x91,0x95,0xe4,0x79,
            0xe7,0xc8,0x37,0x6d, 0x8d,0xd5,0x4e,0xa9,
            0x6c,0x56,0xf4,0xea, 0x65,0x7a,0xae,0x08,
            0xba,0x78,0x25,0x2e, 0x1c,0xa6,0xb4,0xc6,
            0xe8,0xdd,0x74,0x1f, 0x4b,0xbd,0x8b,0x8a,
            0x70,0x3e,0xb5,0x66, 0x48,0x03,0xf6,0x0e,
            0x61,0x35,0x57,0xb9, 0x86,0xc1,0x1d,0x9e,
            0xe1,0xf8,0x98,0x11, 0x69,0xd9,0x8e,0x94,
            0x9b,0x1e,0x87,0xe9, 0xce,0x55,0x28,0xdf,
            0x8c,0xa1,0x89,0x0d, 0xbf,0xe6,0x42,0x68,
            0x41,0x99,0x2d,0x0f, 0xb0,0x54,0xbb,0x16
    };

    // ------------------------ Inverse AES S-Box ------------------------
    public static final int[] INV_SBOX = {
            0x52,0x09,0x6a,0xd5, 0x30,0x36,0xa5,0x38,
            0xbf,0x40,0xa3,0x9e, 0x81,0xf3,0xd7,0xfb,
            0x7c,0xe3,0x39,0x82, 0x9b,0x2f,0xff,0x87,
            0x34,0x8e,0x43,0x44, 0xc4,0xde,0xe9,0xcb,
            0x54,0x7b,0x94,0x32, 0xa6,0xc2,0x23,0x3d,
            0xee,0x4c,0x95,0x0b, 0x42,0xfa,0xc3,0x4e,
            0x08,0x2e,0xa1,0x66, 0x28,0xd9,0x24,0xb2,
            0x76,0x5b,0xa2,0x49, 0x6d,0x8b,0xd1,0x25,
            0x72,0xf8,0xf6,0x64, 0x86,0x68,0x98,0x16,
            0xd4,0xa4,0x5c,0xcc, 0x5d,0x65,0xb6,0x92,
            0x6c,0x70,0x48,0x50, 0xfd,0xed,0xb9,0xda,
            0x5e,0x15,0x46,0x57, 0xa7,0x8d,0x9d,0x84,
            0x90,0xd8,0xab,0x00, 0x8c,0xbc,0xd3,0x0a,
            0xf7,0xe4,0x58,0x05, 0xb8,0xb3,0x45,0x06,
            0xd0,0x2c,0x1e,0x8f, 0xca,0x3f,0x0f,0x02,
            0xc1,0xaf,0xbd,0x03, 0x01,0x13,0x8a,0x6b,
            0x3a,0x91,0x11,0x41, 0x4f,0x67,0xdc,0xea,
            0x97,0xf2,0xcf,0xce, 0xf0,0xb4,0xe6,0x73,
            0x96,0xac,0x74,0x22, 0xe7,0xad,0x35,0x85,
            0xe2,0xf9,0x37,0xe8, 0x1c,0x75,0xdf,0x6e,
            0x47,0xf1,0x1a,0x71, 0x1d,0x29,0xc5,0x89,
            0x6f,0xb7,0x62,0x0e, 0xaa,0x18,0xbe,0x1b,
            0xfc,0x56,0x3e,0x4b, 0xc6,0xd2,0x79,0x20,
            0x9a,0xdb,0xc0,0xfe, 0x78,0xcd,0x5a,0xf4,
            0x1f,0xdd,0xa8,0x33, 0x88,0x07,0xc7,0x31,
            0xb1,0x12,0x10,0x59, 0x27,0x80,0xec,0x5f,
            0x60,0x51,0x7f,0xa9, 0x19,0xb5,0x4a,0x0d,
            0x2d,0xe5,0x7a,0x9f, 0x93,0xc9,0x9c,0xef,
            0xa0,0xe0,0x3b,0x4d, 0xae,0x2a,0xf5,0xb0,
            0xc8,0xeb,0xbb,0x3c, 0x83,0x53,0x99,0x61,
            0x17,0x2b,0x04,0x7e, 0xba,0x77,0xd6,0x26,
            0xe1,0x69,0x14,0x63, 0x55,0x21,0x0c,0x7d
    };

    // ------------------------ Round Constants (Rcon) ------------------------
    public static final int[] RCON = {
            0x01000000,0x02000000,0x04000000,0x08000000,
            0x10000000,0x20000000,0x40000000,0x80000000,
            0x1b000000,0x36000000
    };

    // AES-128: Nk=4, Nr=10, Nb=4 => 44 words total
    public static int[] expandKey(byte[] key) {
        final int Nk = 4, Nr = 10, Nb = 4;
        int totalWords = Nb * (Nr + 1);  // 44
        int[] w = new int[totalWords];

        // Copy the 16-byte key into w[0..3]
        for (int i = 0; i < Nk; i++) {
            w[i] = ((key[4 * i] & 0xff) << 24)
                    | ((key[4 * i + 1] & 0xff) << 16)
                    | ((key[4 * i + 2] & 0xff) << 8)
                    | (key[4 * i + 3] & 0xff);
        }

        // Expand the key
        for (int i = Nk; i < totalWords; i++) {
            int temp = w[i - 1];
            if (i % Nk == 0) {
                temp = subWord(rotWord(temp)) ^ RCON[(i / Nk) - 1];
            }
            w[i] = w[i - Nk] ^ temp;
        }
        return w;
    }

    public static int rotWord(int word) {
        return ((word << 8) & 0xffffffff)
                | ((word >>> 24) & 0xff);
    }

    public static int subWord(int word) {
        int result = 0;
        // For each of the 4 bytes in 'word', apply SBOX
        for (int i = 3; i >= 0; i--) {
            int b = (word >>> (8 * i)) & 0xff;
            result = (result << 8) | SBOX[b];
        }
        return result;
    }

    // ---------- Conversion Helpers ----------
    public static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            out[i / 2] = (byte) (
                    (Character.digit(hex.charAt(i), 16) << 4)
                            |  Character.digit(hex.charAt(i + 1), 16)
            );
        }
        return out;
    }

    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    // Retrieve the round key for a given round from expanded words
    public static byte[][] getRoundKey(int[] expandedWords, int round) {
        byte[][] roundKey = new byte[4][4];
        int start = round * 4;
        for (int c = 0; c < 4; c++) {
            int word = expandedWords[start + c];
            roundKey[0][c] = (byte) ((word >>> 24) & 0xff);
            roundKey[1][c] = (byte) ((word >>> 16) & 0xff);
            roundKey[2][c] = (byte) ((word >>> 8)  & 0xff);
            roundKey[3][c] = (byte) ( word         & 0xff);
        }
        return roundKey;
    }

    // ---------- AES State Conversions (ROW-MAJOR) ----------
    public static byte[][] bytesToState(byte[] block) {
        byte[][] state = new byte[4][4];
        // row-major: first 4 bytes => row 0, next 4 => row 1, etc.
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                state[r][c] = block[r * 4 + c];
            }
        }
        return state;
    }

    public static byte[] stateToBytes(byte[][] state) {
        byte[] block = new byte[16];
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                block[r * 4 + c] = state[r][c];
            }
        }
        return block;
    }

    // ---------- AES Transformations ----------
    public static void subBytes(byte[][] state) {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                state[r][c] = (byte) (SBOX[state[r][c] & 0xff]);
            }
        }
    }

    public static void invSubBytes(byte[][] state) {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                state[r][c] = (byte) (INV_SBOX[state[r][c] & 0xff]);
            }
        }
    }

    public static void shiftRows(byte[][] state) {
        for (int r = 1; r < 4; r++) {
            state[r] = leftRotate(state[r], r);
        }
    }

    public static void invShiftRows(byte[][] state) {
        for (int r = 1; r < 4; r++) {
            state[r] = rightRotate(state[r], r);
        }
    }

    public static void mixColumns(byte[][] state) {
        for (int c = 0; c < 4; c++) {
            byte s0 = state[0][c], s1 = state[1][c],
                    s2 = state[2][c], s3 = state[3][c];
            state[0][c] = (byte) (gmul(s0,2) ^ gmul(s1,3) ^ s2       ^ s3);
            state[1][c] = (byte) (      s0     ^ gmul(s1,2) ^ gmul(s2,3) ^ s3);
            state[2][c] = (byte) (      s0     ^ s1         ^ gmul(s2,2) ^ gmul(s3,3));
            state[3][c] = (byte) (gmul(s0,3) ^ s1           ^ s2         ^ gmul(s3,2));
        }
    }

    public static void invMixColumns(byte[][] state) {
        for (int c = 0; c < 4; c++) {
            byte s0 = state[0][c], s1 = state[1][c],
                    s2 = state[2][c], s3 = state[3][c];
            state[0][c] = (byte) (gmul(s0,14)^ gmul(s1,11)^ gmul(s2,13)^ gmul(s3, 9));
            state[1][c] = (byte) (gmul(s0, 9)^ gmul(s1,14)^ gmul(s2,11)^ gmul(s3,13));
            state[2][c] = (byte) (gmul(s0,13)^ gmul(s1, 9)^ gmul(s2,14)^ gmul(s3,11));
            state[3][c] = (byte) (gmul(s0,11)^ gmul(s1,13)^ gmul(s2, 9)^ gmul(s3,14));
        }
    }

    public static void addRoundKey(byte[][] state, byte[][] roundKey) {
        for (int c = 0; c < 4; c++) {
            for (int r = 0; r < 4; r++) {
                state[r][c] ^= roundKey[r][c];
            }
        }
    }

    public static int gmul(int a, int b) {
        int p = 0;
        for (int i = 0; i < 8; i++) {
            if ((b & 1) != 0) {
                p ^= a;
            }
            boolean hiBit = (a & 0x80) != 0;
            a = (a << 1) & 0xff;
            if (hiBit) {
                a ^= 0x1b;
            }
            b >>= 1;
        }
        return p;
    }

    public static byte[] leftRotate(byte[] row, int shift) {
        byte[] out = new byte[row.length];
        for (int i = 0; i < row.length; i++) {
            out[i] = row[(i + shift) % row.length];
        }
        return out;
    }

    public static byte[] rightRotate(byte[] row, int shift) {
        byte[] out = new byte[row.length];
        int len = row.length;
        for (int i = 0; i < row.length; i++) {
            out[i] = row[(i - shift + len) % len];
        }
        return out;
    }

    // ---------- AES encryption for single block with debug prints ----------
    public static byte[] encryptBlockDetailed(byte[] plaintext, byte[] key) {
        int[] expandedWords = expandKey(key);
        byte[][] state = bytesToState(plaintext);

        System.out.println("Encryption Phase\n");

        // Round 0
        addRoundKey(state, getRoundKey(expandedWords, 0));
        System.out.println("After addRoundKey(0):");
        System.out.println(byteArrayToHexString(stateToBytes(state)));

        // Rounds 1..9
        for (int round = 1; round < 10; round++) {
            subBytes(state);
            System.out.println("After subBytes (" + round + "):");
            System.out.println(byteArrayToHexString(stateToBytes(state)));

            shiftRows(state);
            System.out.println("After shiftRows (" + round + "):");
            System.out.println(byteArrayToHexString(stateToBytes(state)));

            mixColumns(state);
            System.out.println("After mixColumns (" + round + "):");
            System.out.println(byteArrayToHexString(stateToBytes(state)));

            addRoundKey(state, getRoundKey(expandedWords, round));
            System.out.println("After addRoundKey(" + round + "):");
            System.out.println(byteArrayToHexString(stateToBytes(state)));
        }

        // Round 10
        subBytes(state);
        System.out.println("After subBytes (10):");
        System.out.println(byteArrayToHexString(stateToBytes(state)));

        shiftRows(state);
        System.out.println("After shiftRows (10):");
        System.out.println(byteArrayToHexString(stateToBytes(state)));

        addRoundKey(state, getRoundKey(expandedWords, 10));
        System.out.println("After addRoundKey(10):");
        String finalStateHex = byteArrayToHexString(stateToBytes(state));
        System.out.println(finalStateHex);
        System.out.println("Final Ciphertext (hex): " + finalStateHex);

        printCiphertextMatrix(stateToBytes(state));
        return stateToBytes(state);
    }

    // ---------- AES decryption for single block with debug prints ----------
    public static byte[] decryptBlockDetailed(byte[] ciphertext, byte[] key) {
        int[] expandedWords = expandKey(key);
        byte[][] state = bytesToState(ciphertext);

        System.out.println("Decryption Phase\n");

        // Start with round 10
        addRoundKey(state, getRoundKey(expandedWords, 10));
        System.out.println("After addRoundKey(10):");
        System.out.println(byteArrayToHexString(stateToBytes(state)));

        // Rounds 9..1
        for (int round = 9; round >= 1; round--) {
            invShiftRows(state);
            System.out.println("After invShiftRows(" + round + "):");
            System.out.println(byteArrayToHexString(stateToBytes(state)));

            invSubBytes(state);
            System.out.println("After invSubBytes(" + round + "):");
            System.out.println(byteArrayToHexString(stateToBytes(state)));

            addRoundKey(state, getRoundKey(expandedWords, round));
            System.out.println("After addRoundKey(" + round + "):");
            System.out.println(byteArrayToHexString(stateToBytes(state)));

            invMixColumns(state);
            System.out.println("After invMixColumns(" + round + "):");
            System.out.println(byteArrayToHexString(stateToBytes(state)));
        }

        // Final round (0)
        invShiftRows(state);
        System.out.println("After invShiftRows(0):");
        System.out.println(byteArrayToHexString(stateToBytes(state)));

        invSubBytes(state);
        System.out.println("After invSubBytes(0):");
        System.out.println(byteArrayToHexString(stateToBytes(state)));

        addRoundKey(state, getRoundKey(expandedWords, 0));
        System.out.println("After addRoundKey(0):");
        String recoveredHex = byteArrayToHexString(stateToBytes(state));
        System.out.println(recoveredHex);
        System.out.println("Recovered Plaintext (hex): " + recoveredHex);

        printPlaintextMatrix(stateToBytes(state));
        return stateToBytes(state);
    }

    // ---------- Print Functions ----------
    public static void printRoundKeys(int[] expandedWords) {
        System.out.println("All Round Keys (AES-128) => 11 total:");
        for (int round = 0; round <= 10; round++) {
            System.out.printf("Round %d: ", round);
            for (int wordIndex = 4 * round; wordIndex < 4 * round + 4; wordIndex++) {
                int word = expandedWords[wordIndex];
                System.out.printf("%02x %02x %02x %02x ",
                        (word >>> 24) & 0xff, (word >>> 16) & 0xff,
                        (word >>> 8) & 0xff,  word & 0xff);
            }
            System.out.println();
        }
    }

    // If you want to interpret the 4 words as row-major or column-major is optional
    // in 'printRoundKeysRowMajor', but typically AES block is column-based. We'll keep it consistent here
    public static void printRoundKeysRowMajor(int[] expandedWords) {
        System.out.println("\nAll Round Keys in Row-Major Format:");
        for (int round = 0; round <= 10; round++) {
            System.out.printf("Round %d:\n", round);
            byte[][] matrix = new byte[4][4];
            for (int c = 0; c < 4; c++) {
                int word = expandedWords[4 * round + c];
                matrix[0][c] = (byte) ((word >>> 24) & 0xff);
                matrix[1][c] = (byte) ((word >>> 16) & 0xff);
                matrix[2][c] = (byte) ((word >>> 8)  & 0xff);
                matrix[3][c] = (byte) ( word         & 0xff);
            }
            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 4; c++) {
                    System.out.printf("%02x ", matrix[r][c]);
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public static void printExpandedKeyMatrix(int[] expandedWords) {
        System.out.println("Expanded Key (4 rows × 11 columns):");
        for (int row = 0; row < 4; row++) {
            StringBuilder sb = new StringBuilder();
            for (int col = 0; col < 11; col++) {
                int index = row * 11 + col;
                int word = expandedWords[index];
                sb.append(String.format("%08x ", word));
            }
            System.out.println(sb.toString().trim());
        }
    }

    public static void printCiphertextMatrix(byte[] block) {
        System.out.println("\nFinal Ciphertext (Matrix Representation) [Row-Major]:");
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                System.out.printf("%02x ", block[r * 4 + c]);
            }
            System.out.println();
        }
    }

    public static void printPlaintextMatrix(byte[] plaintext) {
        System.out.println("\nRecovered Plaintext (Matrix Representation) [Row-Major]:");
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                System.out.printf("%02x ", plaintext[r * 4 + c]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        System.out.println("=== AES-128 Key Expansion, Round Keys, and ECB Encryption/Decryption Demo ===\n");

        // Example 1: All-zero key and plaintext
        String zeroKeyHex = "00000000000000000000000000000000";
        String plaintextHex = "00000000000000000000000000000000";

        byte[] zeroKey = hexStringToByteArray(zeroKeyHex);
        byte[] plaintext = hexStringToByteArray(plaintextHex);
        int[] expandedZero = expandKey(zeroKey);

        System.out.println("-- Example: Zero Key --");
        System.out.println("Key: " + zeroKeyHex);
        printRoundKeys(expandedZero);
        printRoundKeysRowMajor(expandedZero);

        byte[] finalCiphertextZero = encryptBlockDetailed(plaintext, zeroKey);
        byte[] recoveredZero = decryptBlockDetailed(finalCiphertextZero, zeroKey);

        // Example 2: "Thats my Kung Fu" key (ASCII)
        String exampleKeyHex = "5468617473206d79204b756e67204675";
        byte[] exampleKey = hexStringToByteArray(exampleKeyHex);
        int[] expandedExample = expandKey(exampleKey);

        System.out.println("\n-- Example: \"Thats my Kung Fu\" Key --");
        System.out.println("Key: " + exampleKeyHex);
        printRoundKeys(expandedExample);
        printRoundKeysRowMajor(expandedExample);

        byte[] finalCiphertextEx = encryptBlockDetailed(plaintext, exampleKey);
        byte[] recoveredEx = decryptBlockDetailed(finalCiphertextEx, exampleKey);
    }
}
