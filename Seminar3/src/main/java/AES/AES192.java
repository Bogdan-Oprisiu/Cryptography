package AES;

import static AES.AES128.*;

public class AES192 {

    // Nk=6, Nr=12, Nb=4 => 52 words
    public static int[] expandKey192(byte[] key) {
        final int Nk = 6, Nr = 12, Nb = 4;
        int totalWords = Nb * (Nr + 1); // 52 words
        int[] w = new int[totalWords];

        // Copy the 24-byte key
        for (int i = 0; i < Nk; i++) {
            w[i] = ((key[4 * i] & 0xff) << 24)
                    | ((key[4 * i + 1] & 0xff) << 16)
                    | ((key[4 * i + 2] & 0xff) << 8)
                    | (key[4 * i + 3] & 0xff);
        }

        // Expand
        for (int i = Nk; i < totalWords; i++) {
            int temp = w[i - 1];
            if (i % Nk == 0) {
                temp = subWord(rotWord(temp)) ^ RCON[(i / Nk) - 1];
            }
            w[i] = w[i - Nk] ^ temp;
        }
        return w;
    }

    // Reuse row-major bytesToState, stateToBytes from AES128 or define them again

    public static byte[] encryptBlockDetailed192(byte[] plaintext, byte[] key) {
        final int Nr = 12;
        int[] expandedWords = expandKey192(key);
        byte[][] state = bytesToState(plaintext);

        System.out.println("Encryption Phase (AES-192)\n");

        // Round 0
        addRoundKey(state, getRoundKey(expandedWords, 0));
        System.out.println("After addRoundKey(0):");
        System.out.println(byteArrayToHexString(stateToBytes(state)));

        // Rounds 1..11
        for (int round = 1; round < Nr; round++) {
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

        // Final round (12)
        subBytes(state);
        System.out.println("After subBytes (12):");
        System.out.println(byteArrayToHexString(stateToBytes(state)));

        shiftRows(state);
        System.out.println("After shiftRows (12):");
        System.out.println(byteArrayToHexString(stateToBytes(state)));

        addRoundKey(state, getRoundKey(expandedWords, Nr));
        String finalHex = byteArrayToHexString(stateToBytes(state));
        System.out.println("After addRoundKey(12):");
        System.out.println(finalHex);
        System.out.println("Final Ciphertext (AES-192, hex): " + finalHex);

        printCiphertextMatrix(stateToBytes(state));
        return stateToBytes(state);
    }

    public static byte[] decryptBlockDetailed192(byte[] ciphertext, byte[] key) {
        final int Nr = 12;
        int[] expandedWords = expandKey192(key);
        byte[][] state = bytesToState(ciphertext);

        System.out.println("Decryption Phase (AES-192)\n");

        // Start round 12
        addRoundKey(state, getRoundKey(expandedWords, Nr));
        System.out.println("After addRoundKey(12):");
        System.out.println(byteArrayToHexString(stateToBytes(state)));

        // Rounds 11..1
        for (int round = Nr - 1; round >= 1; round--) {
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
        String recoveredHex = byteArrayToHexString(stateToBytes(state));
        System.out.println("After addRoundKey(0):");
        System.out.println(recoveredHex);
        System.out.println("Recovered Plaintext (AES-192, hex): " + recoveredHex);

        printPlaintextMatrix(stateToBytes(state));
        return stateToBytes(state);
    }
}
