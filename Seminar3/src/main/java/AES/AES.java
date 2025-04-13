package AES;

import java.util.Scanner;

import static AES.AES128.byteArrayToHexString;
import static AES.AES128.hexStringToByteArray;

public class AES {

    public static final int BLOCK_SIZE = 16;

    // -------------------------------------------------
    //   DEMO KEYS for AES-128, AES-192, AES-256 (hex)
    // -------------------------------------------------
    private static final String AES128_KEY =
            "00000000000000000000000000000000";
    private static final String AES192_KEY =
            "000000000000000000000000000000000000000000000000";
    private static final String AES256_KEY =
            "0000000000000000000000000000000000000000000000000000000000000000";

    // -------------------------------------------------
    //              PKCS#7 Padding
    // -------------------------------------------------
    public static byte[] padPKCS7(byte[] input) {
        int padLen = BLOCK_SIZE - (input.length % BLOCK_SIZE);
        if (padLen == 0) {
            padLen = BLOCK_SIZE;
        }
        byte[] out = new byte[input.length + padLen];
        System.arraycopy(input, 0, out, 0, input.length);
        for (int i = input.length; i < out.length; i++) {
            out[i] = (byte) padLen;
        }
        return out;
    }

    public static byte[] unpadPKCS7(byte[] padded) {
        int padLen = padded[padded.length - 1] & 0xff;
        int finalLen = padded.length - padLen;
        byte[] out = new byte[finalLen];
        System.arraycopy(padded, 0, out, 0, finalLen);
        return out;
    }

    // -------------------------------------------------
    //            AES-ECB Encryption/Decryption
    // -------------------------------------------------
    public static byte[] encryptECB(byte[] plaintext, int variant) {
        byte[] padded = padPKCS7(plaintext);
        byte[] ciphertext = new byte[padded.length];

        for (int i = 0; i < padded.length; i += BLOCK_SIZE) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(padded, i, block, 0, BLOCK_SIZE);

            byte[] enc;
            switch (variant) {
                case 128:
                    enc = AES128.encryptBlockDetailed(
                            block,
                            hexStringToByteArray(AES128_KEY)
                    );
                    break;
                case 192:
                    enc = AES192.encryptBlockDetailed192(
                            block,
                            hexStringToByteArray(AES192_KEY)
                    );
                    break;
                case 256:
                    enc = AES256.encryptBlockDetailed256(
                            block,
                            hexStringToByteArray(AES256_KEY)
                    );
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported key size.");
            }
            System.arraycopy(enc, 0, ciphertext, i, BLOCK_SIZE);
        }
        return ciphertext;
    }

    public static byte[] decryptECB(byte[] ciphertext, int variant) {
        byte[] tmp = new byte[ciphertext.length];

        for (int i = 0; i < ciphertext.length; i += BLOCK_SIZE) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(ciphertext, i, block, 0, BLOCK_SIZE);

            byte[] dec;
            switch (variant) {
                case 128:
                    dec = AES128.decryptBlockDetailed(
                            block,
                            hexStringToByteArray(AES128_KEY)
                    );
                    break;
                case 192:
                    dec = AES192.decryptBlockDetailed192(
                            block,
                            hexStringToByteArray(AES192_KEY)
                    );
                    break;
                case 256:
                    dec = AES256.decryptBlockDetailed256(
                            block,
                            hexStringToByteArray(AES256_KEY)
                    );
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported key size.");
            }
            System.arraycopy(dec, 0, tmp, i, BLOCK_SIZE);
        }
        return unpadPKCS7(tmp);
    }

    // -------------------------------------------------
    //            AES-CBC Encryption/Decryption
    // -------------------------------------------------

    /**
     * Encrypts plaintext in AES-CBC mode using a fixed all-zero IV.
     * Debug prints are added to show each block's transformation.
     */
    public static byte[] encryptCBC(byte[] plaintext, int variant) {
        byte[] padded = padPKCS7(plaintext);
        byte[] ciphertext = new byte[padded.length];

        // For simplicity, use a zero IV (16 bytes)
        byte[] iv = new byte[BLOCK_SIZE]; // all zero by default

        for (int i = 0; i < padded.length; i += BLOCK_SIZE) {
            System.out.println("=== Encryption: Processing Block " + (i / BLOCK_SIZE + 1) + " ===");

            // Extract current block from padded plaintext
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(padded, i, block, 0, BLOCK_SIZE);
            System.out.println("Original block (from padded plaintext): " + byteArrayToHexString(block));

            // Show the current IV before XORing
            System.out.println("Current IV: " + byteArrayToHexString(iv));

            // XOR block with IV (this is the input to the AES encryption)
            for (int j = 0; j < BLOCK_SIZE; j++) {
                block[j] ^= iv[j];
            }
            System.out.println("Block after XOR with IV: " + byteArrayToHexString(block));

            // Encrypt the block using the appropriate AES variant
            byte[] enc;
            switch (variant) {
                case 128:
                    enc = AES128.encryptBlockDetailed(
                            block,
                            hexStringToByteArray(AES128_KEY)
                    );
                    break;
                case 192:
                    enc = AES192.encryptBlockDetailed192(
                            block,
                            hexStringToByteArray(AES192_KEY)
                    );
                    break;
                case 256:
                    enc = AES256.encryptBlockDetailed256(
                            block,
                            hexStringToByteArray(AES256_KEY)
                    );
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported key size.");
            }
            System.out.println("Encrypted block: " + byteArrayToHexString(enc));

            // Copy encrypted block to ciphertext array
            System.arraycopy(enc, 0, ciphertext, i, BLOCK_SIZE);

            // Update IV for next block (chaining)
            iv = enc;
            System.out.println();
        }
        return ciphertext;
    }

    /**
     * Decrypts ciphertext in AES-CBC mode using a fixed all-zero IV.
     * Debug prints are added to show each block's transformation.
     */
    public static byte[] decryptCBC(byte[] ciphertext, int variant) {
        if (ciphertext.length % BLOCK_SIZE != 0) {
            throw new IllegalArgumentException("Ciphertext length must be multiple of block size.");
        }

        byte[] tmp = new byte[ciphertext.length];
        byte[] iv = new byte[BLOCK_SIZE]; // zero IV

        for (int i = 0; i < ciphertext.length; i += BLOCK_SIZE) {
            System.out.println("=== Decryption: Processing Block " + (i / BLOCK_SIZE + 1) + " ===");

            // Get the current ciphertext block
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(ciphertext, i, block, 0, BLOCK_SIZE);
            System.out.println("Ciphertext block: " + byteArrayToHexString(block));
            System.out.println("Current IV: " + byteArrayToHexString(iv));

            // Decrypt the ciphertext block using the chosen AES variant
            byte[] dec;
            switch (variant) {
                case 128:
                    dec = AES128.decryptBlockDetailed(
                            block,
                            hexStringToByteArray(AES128_KEY)
                    );
                    break;
                case 192:
                    dec = AES192.decryptBlockDetailed192(
                            block,
                            hexStringToByteArray(AES192_KEY)
                    );
                    break;
                case 256:
                    dec = AES256.decryptBlockDetailed256(
                            block,
                            hexStringToByteArray(AES256_KEY)
                    );
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported key size.");
            }
            System.out.println("Decrypted block (before XOR): " + byteArrayToHexString(dec));

            // XOR decrypted block with IV to retrieve original plaintext block
            for (int j = 0; j < BLOCK_SIZE; j++) {
                dec[j] ^= iv[j];
            }
            System.out.println("Recovered plaintext block after XOR with IV: " + byteArrayToHexString(dec));

            // Copy resulting plaintext block into temporary array
            System.arraycopy(dec, 0, tmp, i, BLOCK_SIZE);

            // Update IV to be the current ciphertext block for next iteration
            iv = block;
            System.out.println();
        }
        return unpadPKCS7(tmp);
    }

    // -------------------------------------------------
    //                   MAIN DEMO
    // -------------------------------------------------
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 1) Choose mode
        String mode;
        while (true) {
            System.out.println("=== AES Mode Demo (ECB / CBC) ===");
            System.out.print("Choose mode (ECB / CBC): ");
            mode = sc.nextLine().trim().toLowerCase();
            if (mode.equals("ecb") || mode.equals("cbc")) {
                break;
            } else {
                System.out.println("Invalid mode. Try again!");
            }
        }

        // 2) Choose AES variant
        int variant;
        while (true) {
            System.out.print("Choose AES variant (128 / 192 / 256): ");
            variant = sc.nextInt();
            sc.nextLine(); // consume leftover newline
            if (variant == 128 || variant == 192 || variant == 256) {
                break;
            } else {
                System.out.println("Invalid key size. Try again!");
            }
        }

        // For demonstration: a test plaintext in hex
        // You could also let the user input this or read from file, etc.
        String plaintextHex = "00112233445566778899AABBCCDDEEFF";
        byte[] plaintext = hexStringToByteArray(plaintextHex);

        byte[] ciphertext;
        byte[] recovered;

        // 3) Encrypt & decrypt depending on chosen mode
        if (mode.equals("ecb")) {
            ciphertext = encryptECB(plaintext, variant);
            recovered = decryptECB(ciphertext, variant);
        } else {
            ciphertext = encryptCBC(plaintext, variant);
            recovered = decryptCBC(ciphertext, variant);
        }

        // 4) Print results
        System.out.println("\n================== RESULTS ==================");
        System.out.println("AES Variant:        " + variant);
        System.out.println("Mode:               " + mode.toUpperCase());
        System.out.println("Plaintext (hex):    " + byteArrayToHexString(plaintext));
        System.out.println("Ciphertext length in bytes = " + ciphertext.length);
        System.out.println("Ciphertext hex length = " + byteArrayToHexString(ciphertext).length());
        System.out.println("Ciphertext (hex): " + byteArrayToHexString(ciphertext));
        System.out.println("Recovered (hex):    " + byteArrayToHexString(recovered));
    }
}
