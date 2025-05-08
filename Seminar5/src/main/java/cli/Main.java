package cli;

import crypto.ElGamalKeyPair;
import crypto.ElGamalSigner;
import crypto.ElGamalVerifier;
import hash.HashResult;
import hash.SaltedHasher;
import stego.LSBSteganography;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.SecureRandom;

public final class Main {

    public static void main(String[] args) throws Exception {

        /* ----------------------------------------------------------------
         * 1.  Sign the ASCII message "B"
         * ---------------------------------------------------------------- */
        var kp = ElGamalKeyPair.fixed();
        var signer = new ElGamalSigner(kp);
        var verifier = new ElGamalVerifier(kp);

        byte[] msg = "B".getBytes(StandardCharsets.US_ASCII);
        var rs = signer.sign(msg, new SecureRandom());

        System.out.println("ElGamal signature:");
        System.out.println(" r = " + rs[0]);
        System.out.println(" s = " + rs[1]);
        System.out.println(" verifies? " + verifier.verify(msg, rs[0], rs[1]));

        /* ----------------------------------------------------------------
         * 2.  Salt + hash the same message
         * ---------------------------------------------------------------- */
        var hasher = new SaltedHasher();          // default 16-byte salt
        HashResult h = hasher.hash(msg);

        System.out.println("\nSalted SHA-256:");
        System.out.println(" salt  = " + h.hexSalt());
        System.out.println(" digest= " + h.hexDigest());

        /* ----------------------------------------------------------------
         * 3.  Embed 256-bit digest into an image
         * ---------------------------------------------------------------- */
        BufferedImage cover = ImageIO.read(
                Main.class.getResourceAsStream("/Tux.jfif")); // make sure it exists
        byte[] digest256 = h.digest();
        BufferedImage stego = LSBSteganography.embed256(digest256, cover);

        Path out = Path.of("image_new.png");
        ImageIO.write(stego, "png", out.toFile());

        System.out.println("\nStego image written to " + out.toAbsolutePath());
        System.out.println(" Extraction check: " +
                java.util.Arrays.equals(digest256, LSBSteganography.extract256(stego)));
    }
}
