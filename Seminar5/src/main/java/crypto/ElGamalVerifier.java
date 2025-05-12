package crypto;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
// MessageDigest and NoSuchAlgorithmException are no longer needed if not hashing
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Verifies ElGamal signatures.
 * For Seminar 5, hashing is skipped (message used directly as 'm').
 */
public final class ElGamalVerifier {

    private final ElGamalKeyPair kp;
    // private final MessageDigest sha256; // Removed - not hashing

    public ElGamalVerifier(ElGamalKeyPair kp) {
        this.kp = Objects.requireNonNull(kp, "KeyPair cannot be null");
        // Hashing setup removed
    }

    /**
     * Verifies an (r, s) signature on the given message bytes.
     * For Seminar 5, this method expects 'm' to be derived directly from the message byte,
     * consistent with how ElGamalSigner (seminar version) creates 'm'.
     *
     * @param message Bytes of the message. For "B", this should be a single byte array [66].
     * @param r       The r component of the signature.
     * @param s       The s component of the signature.
     * @return {@code true} if the signature is valid, {@code false} otherwise.
     */
    public boolean verify(byte[] message, BigInteger r, BigInteger s) {
        Objects.requireNonNull(message, "Message cannot be null");
        Objects.requireNonNull(r, "r cannot be null");
        Objects.requireNonNull(s, "s cannot be null");

        if (message.length != 1) {
            // This simplified version expects a single byte message like "B"
            throw new IllegalArgumentException("This verify method (without hashing) expects a single-byte message for Seminar 5.");
        }

        BigInteger p = kp.p();
        BigInteger pMinus1 = p.subtract(BigInteger.ONE);

        // --- Basic sanity checks on the signature components r and s ---
        // r must be in the range [1, p-1]
        if (r.compareTo(BigInteger.ONE) < 0 || r.compareTo(p) >= 0) {
            System.err.println("Verification failed: r is out of range [1, p-1]. r=" + r + ", p=" + p);
            return false;
        }
        // s must be in the range [1, p-2] (or [0, p-2] depending on convention, but s=0 is problematic)
        // The original ElGamal paper implies 0 < s < p-1.
        if (s.compareTo(BigInteger.ZERO) <= 0 || s.compareTo(pMinus1) >= 0) {
            System.err.println("Verification failed: s is out of range (0, p-1). s=" + s + ", p-1=" + pMinus1);
            return false;
        }

        // --- Step 1 (Modified for Seminar): Get 'm' directly from message byte ---
        // Instead of hashing, use the integer value of the single message byte directly as 'm'.
        // For the message "B", the ASCII value is 66. So, m will be 66.
        BigInteger m = BigInteger.valueOf(message[0] & 0xFF); // Unsigned byte value
        // Check if this integer 'm' is within the valid range [0, p).
        if (m.compareTo(BigInteger.ZERO) < 0 || m.compareTo(p) >= 0) {
            // This check is also present in the signer.
            System.err.println("Verification failed: Message integer m=" + m + " must be between 0 and p=" + p + " (exclusive).");
            return false;
        }

        // --- Verification Check: α^m  ≡  β^r * r^s  (mod p) ---

        // Calculate the left-hand side: α^m mod p
        BigInteger left = kp.alpha().modPow(m, p);

        // Calculate the right-hand side: (β^r * r^s) mod p
        BigInteger betaToR = kp.beta().modPow(r, p); // β^r mod p
        BigInteger rToS = r.modPow(s, p);           // r^s mod p (Note: exponent is s, modulus is p)

        BigInteger right = betaToR.multiply(rToS).mod(p); // (β^r * r^s) mod p

        // The signature is valid if the left-hand side equals the right-hand side.
        boolean isValid = left.equals(right);
        if (!isValid) {
            System.err.println("Verification failed: Left side (" + left + ") does not equal Right side (" + right + ")");
            System.err.println("Parameters used: m=" + m + ", r=" + r + ", s=" + s + ", p=" + p + ", alpha=" + kp.alpha() + ", beta=" + kp.beta());
        }
        return isValid;
    }

    /**
     * Convenience overload for verifying signatures on ASCII strings.
     *
     * @param asciiMessage The ASCII message string (expected to be "B" for Seminar 5).
     * @param r            The r component of the signature.
     * @param s            The s component of the signature.
     * @return {@code true} if the signature is valid, {@code false} otherwise.
     */
    public boolean verify(String asciiMessage, BigInteger r, BigInteger s) {
        Objects.requireNonNull(asciiMessage, "ASCII message cannot be null");
        byte[] messageBytes = asciiMessage.getBytes(StandardCharsets.US_ASCII);
        if (messageBytes.length != 1) {
            throw new IllegalArgumentException("This verify method (without hashing) expects a single-character ASCII string like \"B\".");
        }
        return verify(messageBytes, r, s);
    }
}
