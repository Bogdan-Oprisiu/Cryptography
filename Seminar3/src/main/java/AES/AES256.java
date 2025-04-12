package AES;

import static AES.AES128.*;

public class AES256 {

    // Nk=8, Nr=14, Nb=4 => 60 words
    public static int[] expandKey256(byte[] key) {
        final int Nk = 8, Nr = 14, Nb = 4;
        int totalWords = Nb * (Nr + 1); // 60
        int[] w = new int[totalWords];

        // Copy 32-byte key
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
            } else if (i % Nk == 4) {
                // AES-256 special case
                temp = subWord(temp);
            }
            w[i] = w[i - Nk] ^ temp;
        }
        return w;
    }

    public static byte[] encryptBlockDetailed256(byte[] plaintext, byte[] key) {
        final int Nr = 14;
        int[] expandedWords = expandKey256(key);
        byte[][] state = bytesToState(plaintext);

        System.out.println("Encryption Phase (AES-256)\n");

        // Round 0
        addRoundKey(state, getRoundKey(expandedWords, 0));
        System.out.println("After addRoundKey(0):");
        System.out.println(byteArrayToHexString(stateToBytes(state)));

        // Rounds 1..13
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

        // Final round (14)
        subBytes(state);
        System.out.println("After subBytes (14):");
        System.out.println(byteArrayToHexString(stateToBytes(state)));

        shiftRows(state);
        System.out.println("After shiftRows (14):");
        System.out.println(byteArrayToHexString(stateToBytes(state)));

        addRoundKey(state, getRoundKey(expandedWords, Nr));
        String finalHex = byteArrayToHexString(stateToBytes(state));
        System.out.println("After addRoundKey(14):");
        System.out.println(finalHex);
        System.out.println("Final Ciphertext (AES-256, hex): " + finalHex);

        printCiphertextMatrix(stateToBytes(state));
        return stateToBytes(state);
    }

    public static byte[] decryptBlockDetailed256(byte[] ciphertext, byte[] key) {
        final int Nr = 14;
        int[] expandedWords = expandKey256(key);
        byte[][] state = bytesToState(ciphertext);

        System.out.println("Decryption Phase (AES-256)\n");

        // Round 14
        addRoundKey(state, getRoundKey(expandedWords, Nr));
        System.out.println("After addRoundKey(14):");
        System.out.println(byteArrayToHexString(stateToBytes(state)));

        // Rounds 13..1
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
        System.out.println("Recovered Plaintext (AES-256, hex): " + recoveredHex);

        printPlaintextMatrix(stateToBytes(state));
        return stateToBytes(state);
    }
}
