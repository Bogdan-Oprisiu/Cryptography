package crypto;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Generates an (r, s) pair according to the ElGamal digital signature scheme.
 */
public final class ElGamalSigner {

    private final ElGamalKeyPair kp;
    private final MessageDigest sha256;

    public ElGamalSigner(ElGamalKeyPair kp) {
        this.kp = kp;
        try {
            this.sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    /**
     * Signs the given message bytes.
     *
     * @param message Bytes of the message; use {@code "B".getBytes(StandardCharsets.US_ASCII)} for the lab task.
     * @param rng     Secure {@link SecureRandom} instance.
     * @return a length‑2 array where {@code [0] = r} and {@code [1] = s}.
     */
    public BigInteger[] sign(byte[] message, SecureRandom rng) {
        BigInteger p = kp.p();
        BigInteger pMinus1 = p.subtract(BigInteger.ONE);

        // 1. Hash → integer m
        BigInteger m = new BigInteger(1, sha256.digest(message));

        // 2. Choose random k, 1 < k < p‑1 with gcd(k, p‑1) = 1
        BigInteger k;
        do {
            k = new BigInteger(p.bitLength() - 1, rng).mod(pMinus1);
        } while (k.compareTo(BigInteger.ONE) <= 0 || !k.gcd(pMinus1).equals(BigInteger.ONE));

        // 3. Compute r = alpha^k mod p
        BigInteger r = kp.alpha().modPow(k, p);

        // 4. Compute s = (m − a·r) · k^{-1} mod (p − 1)
        BigInteger kInv = k.modInverse(pMinus1);
        BigInteger s = kInv.multiply(m.subtract(kp.a().multiply(r))).mod(pMinus1);

        return new BigInteger[]{r, s};
    }

    // Convenience overloads --------------------------------------------------

    public BigInteger[] sign(String asciiMessage) {
        return sign(asciiMessage.getBytes(StandardCharsets.US_ASCII), new SecureRandom());
    }
}
