package crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Immutable holder for El Gamal parameters.
 *
 * <ul>
 *   <li><b>p</b>     – prime modulus (lab: 107)</li>
 *   <li><b>alpha</b> – generator (lab: 122 503)</li>
 *   <li><b>a</b>     – private key (lab: 10)</li>
 *   <li><b>beta</b>  – public key = alpha^a mod p</li>
 * </ul>
 */
public record ElGamalKeyPair(BigInteger p,
                             BigInteger alpha,
                             BigInteger a,
                             BigInteger beta) {

    /** Key pair with the exact values given in Seminar 5. */
    public static ElGamalKeyPair fixed() {
        BigInteger p     = new BigInteger("107");
        BigInteger alpha = new BigInteger("122503");
        BigInteger a     = new BigInteger("10");
        BigInteger beta  = alpha.modPow(a, p);           // β = α^a mod p
        return new ElGamalKeyPair(p, alpha, a, beta);
    }

    /**
     * Generates a random (safe-prime) key pair of the requested bit-length.
     * Not required for the assignment but handy for extra experiments.
     */
    public static ElGamalKeyPair random(int bitLength, SecureRandom rng) {
        BigInteger p;
        do {
            p = BigInteger.probablePrime(bitLength, rng);
        } while (!p.subtract(BigInteger.ONE).divide(BigInteger.TWO).isProbablePrime(100));

        BigInteger alpha;
        do {
            alpha = new BigInteger(bitLength - 2, rng).mod(p);
        } while (alpha.signum() == 0);

        BigInteger a = new BigInteger(bitLength - 2, rng)
                .mod(p.subtract(BigInteger.ONE))
                .add(BigInteger.ONE);

        BigInteger beta = alpha.modPow(a, p);

        return new ElGamalKeyPair(p, alpha, a, beta);
    }
}
