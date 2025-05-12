package stego;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Minimal, assignment-oriented LSB steganography helper.
 * Provides methods to embed and extract arbitrary byte[],
 * plus convenience wrappers for 32-byte (256-bit) payloads.
 * Writes/reads the least-significant bit of every RGB channel in row-major order.
 * Alpha channel is preserved if present.
 */
public final class LSBSteganography {

    // Private constructor to prevent instantiation; all methods are static.
    private LSBSteganography() { /* no-instantiation */ }

    /* ------------------------------------------------------------------ */
    /* Core embed / extract methods                                      */
    /* ------------------------------------------------------------------ */

    /**
     * Embed the entire payload into the cover image's LSBs.
     * @param payload  byte array to hide (arbitrary length)
     * @param cover    source image to use as cover (unaltered original)
     * @return deep-copied BufferedImage with payload bits embedded
     * @throws IllegalArgumentException if payload is too large for image capacity
     */
    public static BufferedImage embed(byte[] payload, BufferedImage cover) {
        // Number of bits to embed = payload length * 8
        int bitsNeeded = payload.length * 8;
        // Capacity: width * height * 3 channels (R,G,B) per pixel
        int capacityBits = cover.getWidth() * cover.getHeight() * 3;
        if (bitsNeeded > capacityBits) {
            throw new IllegalArgumentException(
                    "Cover image too small: need " + bitsNeeded + " bits but have " + capacityBits);
        }

        // Create a deep copy of the cover so the original stays unchanged
        BufferedImage stego = new BufferedImage(
                cover.getColorModel(),
                cover.copyData(null),
                cover.isAlphaPremultiplied(),
                null
        );

        int bitPos = 0;                        // current bit position in payload
        int width = stego.getWidth();
        int height = stego.getHeight();

        // Traverse pixels row-major until all bits are embedded
        for (int y = 0; y < height && bitPos < bitsNeeded; y++) {
            for (int x = 0; x < width && bitPos < bitsNeeded; x++) {
                int argb = stego.getRGB(x, y);
                // Extract ARGB components
                int a = (argb >>> 24) & 0xFF;
                int r = (argb >>> 16) & 0xFF;
                int g = (argb >>> 8)  & 0xFF;
                int b = argb & 0xFF;

                // Put R, G, B into an array for easy looping
                int[] chan = { r, g, b };

                // Embed up to 3 bits per pixel (one per channel)
                for (int i = 0; i < 3 && bitPos < bitsNeeded; i++, bitPos++) {
                    // Compute the bit to embed (MSB-first order)
                    int byteIndex = bitPos >>> 3;
                    int bitOffset = 7 - (bitPos & 7);
                    int bit = (payload[byteIndex] >>> bitOffset) & 1;
                    // Clear LSB and set it to our bit
                    chan[i] = (chan[i] & 0xFE) | bit;
                }

                // Reassemble ARGB with modified channels
                int newRGB = (a << 24) | (chan[0] << 16) | (chan[1] << 8) | chan[2];
                stego.setRGB(x, y, newRGB);
            }
        }
        return stego;
    }

    /**
     * Extract a specified number of bits from the image's LSBs, reconstructing a byte array.
     * @param stego    image containing embedded payload
     * @param numBits  number of bits to read (must be <= capacity)
     * @return byte array containing extracted bits (padded to whole bytes)
     * @throws IllegalArgumentException if numBits exceeds image capacity
     */
    public static byte[] extract(BufferedImage stego, int numBits) {
        int capacityBits = stego.getWidth() * stego.getHeight() * 3;
        if (numBits > capacityBits) {
            throw new IllegalArgumentException(
                    "Requested more bits than image holds: " + numBits + " > " + capacityBits);
        }

        byte[] out = new byte[(numBits + 7) / 8];  // allocate enough bytes
        int bitPos = 0;
        int width = stego.getWidth();
        int height = stego.getHeight();

        // Traverse pixels row-major until all bits are extracted
        for (int y = 0; y < height && bitPos < numBits; y++) {
            for (int x = 0; x < width && bitPos < numBits; x++) {
                int rgb = stego.getRGB(x, y);
                int[] chan = { (rgb >>> 16) & 0xFF, (rgb >>> 8) & 0xFF, rgb & 0xFF };

                // Extract up to 3 bits per pixel
                for (int i = 0; i < 3 && bitPos < numBits; i++, bitPos++) {
                    int bit = chan[i] & 1;  // LSB of channel
                    int byteIndex = bitPos >>> 3;
                    int shift = 7 - (bitPos & 7);
                    out[byteIndex] |= (bit << shift);
                }
            }
        }
        return out;
    }

    /* ------------------------------------------------------------------ */
    /* 32-byte convenience wrappers                                       */
    /* ------------------------------------------------------------------ */

    /**
     * Embed exactly 32 bytes (256 bits) into the cover; convenience for fixed-size digests.
     */
    public static BufferedImage embed256(byte[] digest256, BufferedImage cover) {
        if (digest256.length != 32) {
            throw new IllegalArgumentException(
                    "Expected 32-byte digest, got " + digest256.length);
        }
        return embed(digest256, cover);
    }

    /**
     * Extract exactly 256 bits into a 32-byte array; convenience wrapper.
     */
    public static byte[] extract256(BufferedImage stego) {
        return extract(stego, 256);
    }

    /* ------------------------------------------------------------------ */
    /* Manual CLI sanity check                                            */
    /* ------------------------------------------------------------------ */

    /**
     * Self-test demo: loads an example image from resources, embeds 32 bytes
     * of 0xAB, writes the stego PNG, and verifies round-trip.
     */
    public static void main(String[] args) throws Exception {
        try (InputStream is = LSBSteganography.class.getResourceAsStream("/Tux.jfif")) {
            if (is == null) {
                System.err.println("Tux.jfif not found on class-path. " +
                        "Ensure it’s under src/main/resources.");
                return;
            }
            // Read cover image
            BufferedImage cover = ImageIO.read(is);

            // Prepare dummy payload: 32 bytes of 0xAB
            byte[] payload = new byte[32];
            Arrays.fill(payload, (byte) 0xAB);

            // Embed payload
            BufferedImage stego = embed256(payload, cover);

            // Write result as PNG
            Path outPath = Path.of("image_new.png");
            ImageIO.write(stego, "png", outPath.toFile());

            // Extract and verify
            byte[] recovered = extract256(stego);
            System.out.println("Success? " + Arrays.equals(payload, recovered));
            System.out.println("Stego image written to " + outPath.toAbsolutePath());
        }
    }
}
