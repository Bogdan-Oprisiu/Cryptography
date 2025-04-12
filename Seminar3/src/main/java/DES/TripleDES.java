package DES;

public class TripleDES {

    /**
     * Encrypts a single 64-bit block (16 hex digits) using 3DES in EDE mode.
     * 3DES encryption = DES_encrypt(key1, DES_decrypt(key2, DES_encrypt(key3, plaintext))).
     */
    public static String encryptBlock(String plainHex, String key1, String key2, String key3) {
        String step1 = DES.encryptBlock(plainHex, key1);
        String step2 = DES.decryptBlock(step1, key2);
        return DES.encryptBlock(step2, key3);
    }

    /**
     * Decrypts a single 64-bit block (16 hex digits) using 3DES in EDE mode.
     * 3DES decryption = DES_decrypt(key1, DES_encrypt(key2, DES_decrypt(key3, ciphertext))).
     */
    public static String decryptBlock(String cipherHex, String key1, String key2, String key3) {
        String step1 = DES.decryptBlock(cipherHex, key3);
        String step2 = DES.encryptBlock(step1, key2);
        return DES.decryptBlock(step2, key1);
    }

    /**
     * Encrypts an arbitrary text using Triple DES.
     * The text is converted into ASCII hex, zero-padded to a multiple of 16 hex digits (8 bytes),
     * then segmented into 64-bit blocks, and each is processed using encryptBlock(...).
     */
    public static String encryptText(String text, String key1, String key2, String key3) {
        String asciiHex = DES.textToHex(text);
        asciiHex = DES.padHex(asciiHex);
        System.out.println("[INFO] Text as hex: " + asciiHex);
        StringBuilder cipherBuilder = new StringBuilder();
        for (int i = 0; i < asciiHex.length(); i += 16) {
            String block = asciiHex.substring(i, i + 16);
            System.out.println("\n--- Encrypting block: " + block + " ---");
            String cipherBlock = encryptBlock(block, key1, key2, key3);
            cipherBuilder.append(cipherBlock);
        }
        return cipherBuilder.toString();
    }

    /**
     * Decrypts the 3DES ciphertext (hex) and converts it back into the original text.
     */
    public static String decryptText(String cipherTextHex, String key1, String key2, String key3) {
        StringBuilder plainHexBuilder = new StringBuilder();
        for (int i = 0; i < cipherTextHex.length(); i += 16) {
            String block = cipherTextHex.substring(i, i + 16);
            String plainBlock = decryptBlock(block, key1, key2, key3);
            plainHexBuilder.append(plainBlock);
        }
        String unpaddedHex = DES.unpadHex(plainHexBuilder.toString());
        return DES.hexToString(unpaddedHex);
    }

    public static void main(String[] args) {
        // Example Triple DES keys (each 16 hex digits = 64 bits)
        String key1 = "0123456789abcdef";
        String key2 = "23456789abcdef01";
        String key3 = "456789abcdef0123";

        // Test 3DES on an arbitrary text.
        String plaintext = "Triple DES works!";
        System.out.println("=== Triple DES Encryption ===");
        System.out.println("Plaintext: " + plaintext);
        String cipherTextHex = encryptText(plaintext, key1, key2, key3);
        System.out.println("\n[FINAL] Encrypted Text (hex): " + cipherTextHex);

        System.out.println("\n=== Triple DES Decryption ===");
        String recoveredText = decryptText(cipherTextHex, key1, key2, key3);
        System.out.println("Decrypted Text: " + recoveredText);
    }
}
