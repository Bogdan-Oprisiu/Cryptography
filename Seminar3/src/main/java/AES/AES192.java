package AES;

import static AES.AES128.*;

public class AES192 {

    // Nk = 6, Nr = 12, Nb = 4 => 52 words
    public static int[] expandKey192(byte[] key) {
        final int Nk = 6, Nr = 12, Nb = 4;
        int totalWords = Nb * (Nr + 1); // 52 words
        int[] w = new int[totalWords];

        // Copy the 24-byte key into first Nk words
        for (int i = 0; i < Nk; i++) {
            w[i] = ((key[4 * i] & 0xff) << 24)
                    | ((key[4 * i + 1] & 0xff) << 16)
                    | ((key[4 * i + 2] & 0xff) << 8)
                    | (key[4 * i + 3] & 0xff);
        }

        // Key expansion
        for (int i = Nk; i < totalWords; i++) {
            int temp = w[i - 1];
            if (i % Nk == 0) {
                temp = subWord(rotWord(temp)) ^ RCON[(i / Nk) - 1];
            }
            w[i] = w[i - Nk] ^ temp;
        }

        return w;
    }

    public static byte[] encryptBlockDetailed192(byte[] plaintext, byte[] key) {
        final int Nr = 12;
        final int Nk = 6;
        int[] expanded = expandKey192(key);
        byte[][] state = bytesToState(plaintext);

        System.out.println("Encryption Phase (AES-192)\n");

        addRoundKey(state, getRoundKey(expanded, 0, Nk));
        System.out.println("After addRoundKey(0):");
        System.out.println(byteArrayToHexString(stateToBytes(state)));

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

            addRoundKey(state, getRoundKey(expanded, round, Nk));
            System.out.println("After addRoundKey(" + round + "):");
            System.out.println(byteArrayToHexString(stateToBytes(state)));
        }

        // Final Round
        subBytes(state);
        System.out.println("After subBytes (12):");
        System.out.println(byteArrayToHexString(stateToBytes(state)));

        shiftRows(state);
        System.out.println("After shiftRows (12):");
        System.out.println(byteArrayToHexString(stateToBytes(state)));

        addRoundKey(state, getRoundKey(expanded, Nr, Nk));
        String finalHex = byteArrayToHexString(stateToBytes(state));
        System.out.println("After addRoundKey(12):");
        System.out.println(finalHex);
        System.out.println("Final Ciphertext (AES-192, hex): " + finalHex);

        printCiphertextMatrix(stateToBytes(state));
        return stateToBytes(state);
    }

    public static byte[] decryptBlockDetailed192(byte[] ciphertext, byte[] key) {
        final int Nr = 12;
        final int Nk = 6;
        int[] expanded = expandKey192(key);
        byte[][] state = bytesToState(ciphertext);

        System.out.println("Decryption Phase (AES-192)\n");

        addRoundKey(state, getRoundKey(expanded, Nr, Nk));
        System.out.println("After addRoundKey(12):");
        System.out.println(byteArrayToHexString(stateToBytes(state)));

        for (int round = Nr - 1; round >= 1; round--) {
            invShiftRows(state);
            System.out.println("After invShiftRows(" + round + "):");
            System.out.println(byteArrayToHexString(stateToBytes(state)));

            invSubBytes(state);
            System.out.println("After invSubBytes(" + round + "):");
            System.out.println(byteArrayToHexString(stateToBytes(state)));

            addRoundKey(state, getRoundKey(expanded, round, Nk));
            System.out.println("After addRoundKey(" + round + "):");
            System.out.println(byteArrayToHexString(stateToBytes(state)));

            invMixColumns(state);
            System.out.println("After invMixColumns(" + round + "):");
            System.out.println(byteArrayToHexString(stateToBytes(state)));
        }

        invShiftRows(state);
        System.out.println("After invShiftRows(0):");
        System.out.println(byteArrayToHexString(stateToBytes(state)));

        invSubBytes(state);
        System.out.println("After invSubBytes(0):");
        System.out.println(byteArrayToHexString(stateToBytes(state)));

        addRoundKey(state, getRoundKey(expanded, 0, Nk));
        String recoveredHex = byteArrayToHexString(stateToBytes(state));
        System.out.println("After addRoundKey(0):");
        System.out.println(recoveredHex);
        System.out.println("Recovered Plaintext (AES-192, hex): " + recoveredHex);

        printPlaintextMatrix(stateToBytes(state));
        return stateToBytes(state);
    }

    public static void main(String[] args) {
        System.out.println("=== AES-192 Column-Major Implementation Demo ===\n");

        String keyHex = "000000000000000000000000000000000000000000000000";
        String plaintextHex = "00112233445566778899aabbccddeeff";

        byte[] key = hexStringToByteArray(keyHex);
        byte[] plaintext = hexStringToByteArray(plaintextHex);

        byte[] encrypted = encryptBlockDetailed192(plaintext, key);
        byte[] decrypted = decryptBlockDetailed192(encrypted, key);
    }
}
