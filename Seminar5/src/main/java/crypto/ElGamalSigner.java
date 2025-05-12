package crypto;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Generates ElGamal signature (r, s).
 */
public final class ElGamalSigner {

    // ElGamal key parameters (p, alpha, a, beta)
    private final ElGamalKeyPair kp;

    /**
     * Constructor.
     *
     * @param kp The ElGamalKeyPair (p, alpha, private key 'a', beta). Not null.
     */
    public ElGamalSigner(ElGamalKeyPair kp) {
        this.kp = Objects.requireNonNull(kp, "KeyPair cannot be null");
    }

    /**
     * Signs message bytes using fixed k=45 and message byte as 'm' (no hashing).
     *
     * @param message Bytes of the message (e.g., "B" -> [66] for Seminar 5).
     * @return Signature [r, s].
     * @throws IllegalArgumentException if k=45 is invalid for key params,
     * or message is not a single byte, or m is out of range [0, p).
     */
    public BigInteger[] sign(byte[] message) {
        Objects.requireNonNull(message, "Message cannot be null");

        // ElGamal parameters
        BigInteger p = kp.p(); // Prime modulus
        BigInteger pMinus1 = p.subtract(BigInteger.ONE); // p-1, for operations in Z_p-1^*

        // Seminar Requirement: Fixed k=45.
        BigInteger k = BigInteger.valueOf(45);

        // Validate k: 0 < k < p-1 and gcd(k, p-1) = 1
        if (k.compareTo(BigInteger.ZERO) <= 0 || k.compareTo(pMinus1) >= 0) {
            throw new IllegalArgumentException("Fixed k=" + k + " must be in (0, p-1). p-1=" + pMinus1);
        }
        if (!k.gcd(pMinus1).equals(BigInteger.ONE)) {
            throw new IllegalArgumentException("gcd(k, p-1) must be 1. k=" + k + ", p-1=" + pMinus1);
        }
        // For p=107 (from ElGamalKeyPair.fixed()), p-1=106. gcd(45, 106) = 1.

        // 'B' (ASCII 66) -> m = 66.
        BigInteger m = BigInteger.valueOf(message[0] & 0xFF); // Unsigned byte value
        // Validate m: 0 <= m < p
        if (m.compareTo(BigInteger.ZERO) < 0 || m.compareTo(p) >= 0) {
            throw new IllegalArgumentException("Message integer m=" + m + " must be in [0, p). p=" + p);
        }

        // Step 3: Compute r = alpha^k mod p
        BigInteger r = kp.alpha().modPow(k, p);

        // Step 4: Compute s = (m - a*r) * k^(-1) mod (p-1)
        BigInteger kInv = k.modInverse(pMinus1);       // k^-1 mod (p-1)
        BigInteger ar = kp.a().multiply(r);            // private key 'a' * r
        BigInteger mMinusAr = m.subtract(ar);          // m - a*r
        BigInteger product = kInv.multiply(mMinusAr);  // (m - a*r) * k^-1
        BigInteger s = product.mod(pMinus1);           // result mod (p-1)

        // Signature is the pair (r, s)
        return new BigInteger[]{r, s};
    }

    /**
     * Convenience overload: signs single-character ASCII string (fixed k=45, no hashing).
     *
     * @param asciiMessage Single-character ASCII string (e.g., "B").
     * @return Signature [r, s].
     */
    public BigInteger[] sign(String asciiMessage) {
        Objects.requireNonNull(asciiMessage, "ASCII message cannot be null");
        byte[] messageBytes = asciiMessage.getBytes(StandardCharsets.US_ASCII);
        if (messageBytes.length != 1) { // Ensure it's a single character for this specific seminar setup
            throw new IllegalArgumentException("Sign method (no hashing) expects a single-character ASCII string.");
        }
        return sign(messageBytes);
    }
}
