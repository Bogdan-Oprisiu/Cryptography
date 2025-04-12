package AES;

import java.util.Scanner;

import static AES.AES128.byteArrayToHexString;
import static AES.AES128.hexStringToByteArray;

public class AES {

    public static final int BLOCK_SIZE = 16; // AES block size

    // Predefined keys in hex
    private static final String AES128_KEY = "00000000000000000000000000000000";
    private static final String AES192_KEY = "000000000000000000000000000000000000000000000000";
    private static final String AES256_KEY = "0000000000000000000000000000000000000000000000000000000000000000";

    // Standard PKCS#7 padding
    public static byte[] padPKCS7(byte[] input) {
        int padLength = BLOCK_SIZE - (input.length % BLOCK_SIZE);
        if (padLength == 0) padLength = BLOCK_SIZE;
        byte[] padded = new byte[input.length + padLength];
        System.arraycopy(input, 0, padded, 0, input.length);
        for (int i = input.length; i < padded.length; i++) {
            padded[i] = (byte) padLength;
        }
        return padded;
    }

    public static byte[] unpadPKCS7(byte[] padded) {
        int padLength = padded[padded.length - 1] & 0xff;
        byte[] out = new byte[padded.length - padLength];
        System.arraycopy(padded, 0, out, 0, out.length);
        return out;
    }

    // ECB encryption
    public static byte[] encryptECB(byte[] plaintext, int aesVariant) {
        byte[] padded = padPKCS7(plaintext);
        byte[] ciphertext = new byte[padded.length];

        for (int i = 0; i < padded.length; i += BLOCK_SIZE) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(padded, i, block, 0, BLOCK_SIZE);
            byte[] encBlock;
            switch (aesVariant) {
                case 128:
                    encBlock = AES128.encryptBlockDetailed(block,
                            hexStringToByteArray(AES128_KEY));
                    break;
                case 192:
                    encBlock = AES192.encryptBlockDetailed192(block,
                            hexStringToByteArray(AES192_KEY));
                    break;
                case 256:
                    encBlock = AES256.encryptBlockDetailed256(block,
                            hexStringToByteArray(AES256_KEY));
                    break;
                default:
                    // fallback
                    encBlock = AES128.encryptBlockDetailed(block,
                            hexStringToByteArray(AES128_KEY));
            }
            System.arraycopy(encBlock, 0, ciphertext, i, BLOCK_SIZE);
        }
        return ciphertext;
    }

    // ECB decryption
    public static byte[] decryptECB(byte[] ciphertext, int aesVariant) {
        byte[] paddedPlaintext = new byte[ciphertext.length];

        for (int i = 0; i < ciphertext.length; i += BLOCK_SIZE) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(ciphertext, i, block, 0, BLOCK_SIZE);
            byte[] decBlock;
            switch (aesVariant) {
                case 128:
                    decBlock = AES128.decryptBlockDetailed(block,
                            hexStringToByteArray(AES128_KEY));
                    break;
                case 192:
                    decBlock = AES192.decryptBlockDetailed192(block,
                            hexStringToByteArray(AES192_KEY));
                    break;
                case 256:
                    decBlock = AES256.decryptBlockDetailed256(block,
                            hexStringToByteArray(AES256_KEY));
                    break;
                default:
                    decBlock = AES128.decryptBlockDetailed(block,
                            hexStringToByteArray(AES128_KEY));
            }
            System.arraycopy(decBlock, 0, paddedPlaintext, i, BLOCK_SIZE);
        }
        return unpadPKCS7(paddedPlaintext);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int variant;
        while (true) {
            System.out.println("=== AES ECB Mode Demo ===");
            System.out.print("Choose AES variant (128, 192, or 256): ");
            variant = sc.nextInt();
            if (variant == 128 || variant == 192 || variant == 256) {
                break;
            } else {
                System.out.println("Invalid input. Try again!");
            }
        }
        sc.nextLine(); // consume leftover newline


        String plaintextHex;
        plaintextHex = "00112233445566778899AABBCCDDEEFF";
//        plaintextHex = "00000000000000000000000000000000";

        byte[] plaintext = hexStringToByteArray(plaintextHex);

        // Encrypt
        byte[] ciphertext = encryptECB(plaintext, variant);

        // Decrypt
        byte[] recoveredPlaintext = decryptECB(ciphertext, variant);

        System.out.println("\n\n\n\n\n");
        System.out.println("====================================");
        System.out.println("Plaintext (hex): " + byteArrayToHexString(plaintext));
        System.out.println("Ciphertext (hex): " + byteArrayToHexString(ciphertext));
        System.out.println("Recovered plaintext (hex): " + byteArrayToHexString(recoveredPlaintext));
    }
}
