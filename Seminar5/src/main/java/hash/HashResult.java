package hash;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import util.ByteUtils;

/**
 * Immutable container for a randomly-generated salt and its corresponding
 * SHA-256 digest. Provides utility methods for encoding both salt and digest
 * in hex or Base64, and concatenating them for ease of use in embedding
 * workflows (e.g., steganography).
 */
public record HashResult(byte[] salt, byte[] digest) {

    // ------------------------------------------------------------------
    // Hexadecimal encoding helpers
    // ------------------------------------------------------------------

    /**
     * Convert the salt to a lowercase hex string.
     *
     * @return hex-encoded salt
     */
    public String saltHex() {
        return Hex.encodeHexString(salt);
    }

    /**
     * Convert the digest to a lowercase hex string.
     * @return hex-encoded digest
     */
    public String digestHex() {
        return Hex.encodeHexString(digest);
    }

    // ------------------------------------------------------------------
    // Aliases for backward compatibility
    // ------------------------------------------------------------------

    /**
     * Alias for {@link #saltHex()}, retained so tests and external callers
     * expecting hexSalt() continue to work without changes.
     * @return hex-encoded salt
     */
    public String hexSalt() {
        return saltHex();
    }

    /**
     * Alias for {@link #digestHex()}, retained so tests and external callers
     * expecting hexDigest() continue to work without changes.
     * @return hex-encoded digest
     */
    public String hexDigest() {
        return digestHex();
    }

    // ------------------------------------------------------------------
    // Base64 encoding helpers
    // ------------------------------------------------------------------

    /**
     * Convert the salt to a Base64-encoded string.
     * @return Base64-encoded salt
     */
    public String saltBase64() {
        return Base64.encodeBase64String(salt);
    }

    /**
     * Convert the digest to a Base64-encoded string.
     * @return Base64-encoded digest
     */
    public String digestBase64() {
        return Base64.encodeBase64String(digest);
    }

    // ------------------------------------------------------------------
    // Concatenation helper
    // ------------------------------------------------------------------

    /**
     * Concatenate the salt and digest into one byte array (salt || digest).
     * Handy when you need to embed both values together in another medium.
     * @return new byte array containing salt followed by digest
     */
    public byte[] concatenated() {
        return ByteUtils.concat(salt, digest);
    }

    // ------------------------------------------------------------------
    // Debug-friendly string representation
    // ------------------------------------------------------------------

    /**
     * Returns a concise, human-readable representation useful for logging.
     * Format: "salt=<hex>  digest=<hex>".
     * @return string combining hex-encoded salt and digest
     */
    @Override
    public String toString() {
        return "salt=" + saltHex() + "  digest=" + digestHex();
    }
}
