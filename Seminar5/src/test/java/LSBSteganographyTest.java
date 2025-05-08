import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import stego.LSBSteganography;

/**
 * Unit tests for LSBSteganography: round-trip correctness,
 * cover immutability, and capacity check.
 */
class LSBSteganographyTest {

    // ------------------------------------------------------------------ //
    // 1.  32-byte payload round-trips correctly                           //
    // ------------------------------------------------------------------ //
    @Test
    void roundTripPayload32() {
        byte[] payload = new byte[32];
        Arrays.fill(payload, (byte) 0xAB);           // any non-trivial pattern

        BufferedImage cover = blankRGB(20, 20, Color.WHITE); // 400 px → 1200 bits capacity
        int samplePixel = cover.getRGB(0, 0);               // remember for immutability check

        BufferedImage stego = LSBSteganography.embed256(payload, cover);
        byte[] recovered    = LSBSteganography.extract256(stego);

        assertArrayEquals(payload, recovered, "Extracted payload must equal original");
        assertEquals(samplePixel, cover.getRGB(0, 0), "Embed must not mutate the cover image");
        // and stego should differ from cover at least somewhere
        assertNotEquals(samplePixel, stego.getRGB(0, 0), "Stego image should have altered LSBs");
    }

    // ------------------------------------------------------------------ //
    // 2.  Capacity check: too-small image should throw                    //
    // ------------------------------------------------------------------ //
    @Test
    void throwsWhenCoverTooSmall() {
        byte[] payload = new byte[32];                   // 256 bits needed
        BufferedImage tinyCover = blankRGB(5, 5, Color.WHITE); // 25 px → 75 bits

        assertThrows(IllegalArgumentException.class,
                () -> LSBSteganography.embed256(payload, tinyCover),
                "Embedding should fail when capacity is insufficient");
    }

    // ------------------------------------------------------------------ //
    // Helper: create a solid-colour RGB image                             //
    // ------------------------------------------------------------------ //
    private static BufferedImage blankRGB(int w, int h, Color c) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int rgb = c.getRGB();
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                img.setRGB(x, y, rgb);
        return img;
    }
}
