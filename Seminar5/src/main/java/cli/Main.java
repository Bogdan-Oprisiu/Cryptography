package cli;

import crypto.ElGamalKeyPair;
import crypto.ElGamalSigner;
import crypto.ElGamalVerifier;
import hash.HashResult;
import hash.SaltedHasher;
import stego.LSBSteganography;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;

public final class Main {

    public static void main(String[] args) throws Exception {

        /* ===========================================================
         * 1)  El Gamal — sign single-byte ASCII message “B”
         * ===========================================================
         */
        var kp       = ElGamalKeyPair.fixed();                 // p =107, α =122 503, a =10
        var signer   = new ElGamalSigner(kp);
        var verifier = new ElGamalVerifier(kp);

        byte[] msg   = "B".getBytes(StandardCharsets.US_ASCII);
        BigInteger[] sig = signer.sign(msg);                   // k is hard-wired to 45 in ElGamalSigner
        boolean ok   = verifier.verify(msg, sig[0], sig[1]);

        // (a) verify β = α^a mod p
        BigInteger betaCheck = kp.alpha().modPow(kp.a(), kp.p());
        System.out.println("β matches key-pair? " + betaCheck.equals(kp.beta()));

        System.out.printf("Signature  r = %s%n", sig[0]);
        System.out.printf("Signature  s = %s%n", sig[1]);
        System.out.println("Signature verifies? " + ok);

        /* ===========================================================
         * 2)  Salt + SHA-256 hash
         *     – we hash “B” + r + s
         * ===========================================================
         */
        var hasher         = new SaltedHasher();               // default: 16-byte random salt
        String concat      = "B" + sig[0] + sig[1];            // simple decimal concatenation
        HashResult h       = hasher.hash(concat.getBytes(StandardCharsets.US_ASCII));

        System.out.println("\nSalt (hex)   : " + h.hexSalt());
        System.out.println("Digest (hex) : " + h.hexDigest());
        byte[] digest256 = h.digest();                         // = 32 bytes

        /* ===========================================================
         * 3)  Embed 256-bit digest into least-significant bits of RGB
         * ===========================================================
         */
        Path in  = Path.of("C:\\Uni\\Cryptography\\Seminar5\\src\\main\\resources\\Mickey.jfif");
        Path out = Path.of("image_new.jpg");

        BufferedImage cover = ImageIO.read(in.toFile());
        BufferedImage stego = LSBSteganography.embed256(digest256, cover);
        ImageIO.write(stego, "jpg", out.toFile());

        // quick self-check
        boolean identical = Arrays.equals(
                digest256,
                LSBSteganography.extract256(stego));
        System.out.println("\nStego written to  " + out.toAbsolutePath());
        System.out.println("Extraction OK?     " + identical);
    }
}
