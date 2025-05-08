package crypto;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Verifies ElGamal signatures.
 */
public final class ElGamalVerifier {

    private final ElGamalKeyPair kp;
    private final MessageDigest sha256;

    public ElGamalVerifier(ElGamalKeyPair kp) {
        this.kp = kp;
        try {
            this.sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    /**
     * Verifies an (r, s) signature on the given message bytes.
     *
     * @return {@code true} if valid, {@code false} otherwise.
     */
    public boolean verify(byte[] message, BigInteger r, BigInteger s) {
        BigInteger p = kp.p();
        BigInteger pMinus1 = p.subtract(BigInteger.ONE);

        // Basic sanity checks on the signature components
        if (r.compareTo(BigInteger.ONE) < 0 || r.compareTo(p) >= 0) return false;
        if (s.compareTo(BigInteger.ZERO) <= 0 || s.compareTo(pMinus1) >= 0) return false;

        BigInteger m = new BigInteger(1, sha256.digest(message));

        // α^m  ?=  β^r · r^s   (mod p)
        BigInteger left = kp.alpha().modPow(m, p);
        BigInteger right = kp.beta().modPow(r, p)
                .multiply(r.modPow(s, p))
                .mod(p);
        return left.equals(right);
    }

    // Convenience overload ---------------------------------------------------

    public boolean verify(String asciiMessage, BigInteger r, BigInteger s) {
        return verify(asciiMessage.getBytes(StandardCharsets.US_ASCII), r, s);
    }
}
