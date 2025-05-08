package hash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Generates a random salt, prepends it to the message and returns SHA‑256(salt‖msg).
 */
public final class SaltedHasher {

    private final int saltLen;
    private final SecureRandom rng;
    private final MessageDigest sha256;

    /**
     * @param saltLengthBytes length of salt in bytes (16–32 recommended)
     * @param rng             cryptographically‑secure RNG
     */
    public SaltedHasher(int saltLengthBytes, SecureRandom rng) {
        if (saltLengthBytes <= 0) throw new IllegalArgumentException("Salt length must be > 0");
        this.saltLen = saltLengthBytes;
        this.rng = rng;
        try {
            this.sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not present", e);
        }
    }

    /** Creates a hasher with 16‑byte salts and a fresh {@link SecureRandom}. */
    public SaltedHasher() {
        this(16, new SecureRandom());
    }

    /** Convenience: custom salt length but default RNG. */
    public SaltedHasher(int saltLengthBytes) {
        this(saltLengthBytes, new SecureRandom());
    }

    // ----------------------------------------------------------------
    // Public API ------------------------------------------------------
    // ----------------------------------------------------------------

    /** Hashes arbitrary bytes, returning salt + digest. */
    public HashResult hash(byte[] message) {
        byte[] salt = new byte[saltLen];
        rng.nextBytes(salt);

        sha256.reset();
        sha256.update(salt);                // salt || message
        sha256.update(message);
        byte[] digest = sha256.digest();

        return new HashResult(salt, digest);
    }

    /** Convenience wrapper for ASCII strings. */
    public HashResult hash(String asciiMessage) {
        return hash(asciiMessage.getBytes(StandardCharsets.US_ASCII));
    }

    // ----------------------------------------------------------------
    // Static helpers --------------------------------------------------
    // ----------------------------------------------------------------

    /** One‑liner helper using default 16‑byte salts. */
    public static HashResult sha256Salt(byte[] message) {
        return new SaltedHasher().hash(message);
    }
}
