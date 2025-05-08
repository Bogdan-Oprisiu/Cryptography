package stego;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Minimal, assignment-oriented LSB steganography helper.
 * Writes/reads the least-significant bit of every RGB channel in row-major
 * order. Alpha, if present, is preserved.
 */
public final class LSBSteganography {

    private LSBSteganography() {/* no-instantiation */}

    /* ------------------------------------------------------------------ */
    /* Core embed / extract                                               */
    /* ------------------------------------------------------------------ */

    public static BufferedImage embed(byte[] payload, BufferedImage cover) {
        int bitsNeeded = payload.length * 8;
        int capacityBits = cover.getWidth() * cover.getHeight() * 3;   // 3 channels per pixel
        if (bitsNeeded > capacityBits)
            throw new IllegalArgumentException(
                    "Cover image too small: need " + bitsNeeded + " bits but have " + capacityBits);

        // deep-copy the cover so original stays unchanged
        BufferedImage stego = new BufferedImage(
                cover.getColorModel(),
                cover.copyData(null),
                cover.isAlphaPremultiplied(),
                null);

        int bitPos = 0, width = stego.getWidth(), height = stego.getHeight();

        for (int y = 0; y < height && bitPos < bitsNeeded; y++) {
            for (int x = 0; x < width && bitPos < bitsNeeded; x++) {
                int argb = stego.getRGB(x, y);
                int a = (argb >>> 24) & 0xFF;
                int r = (argb >>> 16) & 0xFF;
                int g = (argb >>> 8) & 0xFF;
                int b = argb & 0xFF;

                int[] chan = {r, g, b};
                for (int i = 0; i < 3 && bitPos < bitsNeeded; i++, bitPos++) {
                    int bit = (payload[bitPos >>> 3] >>> (7 - (bitPos & 7))) & 1; // MSB-first
                    chan[i] = (chan[i] & 0xFE) | bit;                           // set LSB
                }
                int newRGB = (a << 24) | (chan[0] << 16) | (chan[1] << 8) | chan[2];
                stego.setRGB(x, y, newRGB);
            }
        }
        return stego;
    }

    public static byte[] extract(BufferedImage stego, int numBits) {
        int capacityBits = stego.getWidth() * stego.getHeight() * 3;
        if (numBits > capacityBits)
            throw new IllegalArgumentException("Requested more bits than image holds");

        byte[] out = new byte[(numBits + 7) / 8];
        int bitPos = 0, width = stego.getWidth(), height = stego.getHeight();

        for (int y = 0; y < height && bitPos < numBits; y++) {
            for (int x = 0; x < width && bitPos < numBits; x++) {
                int rgb = stego.getRGB(x, y);
                int[] chan = {(rgb >>> 16) & 0xFF, (rgb >>> 8) & 0xFF, rgb & 0xFF};

                for (int i = 0; i < 3 && bitPos < numBits; i++, bitPos++) {
                    int bit = chan[i] & 1;
                    int byteIndex = bitPos >>> 3;
                    int shift = 7 - (bitPos & 7);
                    out[byteIndex] |= bit << shift;
                }
            }
        }
        return out;
    }

    /* ------------------------------------------------------------------ */
    /* 32-byte convenience wrappers                                       */
    /* ------------------------------------------------------------------ */

    public static BufferedImage embed256(byte[] digest256, BufferedImage cover) {
        if (digest256.length != 32)
            throw new IllegalArgumentException("Expected 32-byte digest, got " + digest256.length);
        return embed(digest256, cover);
    }

    public static byte[] extract256(BufferedImage stego) {
        return extract(stego, 256);
    }

    /* ------------------------------------------------------------------ */
    /* Manual CLI sanity check                                            */
    /* ------------------------------------------------------------------ */
    public static void main(String[] args) throws Exception {
        // load cover from resources
        try (InputStream is = LSBSteganography.class.getResourceAsStream("/Tux.jfif")) {
            if (is == null) {
                System.err.println("Tux.jfif not found on class-path. " +
                        "Ensure it’s under src/main/resources.");
                return;
            }
            BufferedImage cover = ImageIO.read(is);

            // demo payload: 32 bytes of 0xAB
            byte[] payload = new byte[32];
            Arrays.fill(payload, (byte) 0xAB);

            BufferedImage stego = embed256(payload, cover);

            // write result next to where you run the program
            Path outPath = Path.of("image_new.png");
            ImageIO.write(stego, "png", outPath.toFile());

            // verify round-trip
            byte[] recovered = extract256(stego);
            System.out.println("Success? " + Arrays.equals(payload, recovered));
            System.out.println("Stego image written to " + outPath.toAbsolutePath());
        }
    }

}
