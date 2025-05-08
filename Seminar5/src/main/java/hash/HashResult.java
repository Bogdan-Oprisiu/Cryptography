package hash;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import util.ByteUtils;

/**
 * Immutable holder for the random {@code salt} and the resulting SHA-256
 * {@code digest}.  Convenience encoders keep other classes boiler-plate free.
 */
public record HashResult(byte[] salt, byte[] digest) {

    /* ---------- Hex helpers (original names) ---------- */

    public String saltHex() {
        return Hex.encodeHexString(salt);
    }

    public String digestHex() {
        return Hex.encodeHexString(digest);
    }

    /* ---------- Hex helpers (aliases some tests expect) ---------- */

    public String hexSalt() {
        return saltHex();
    }

    public String hexDigest() {
        return digestHex();
    }

    /* ---------- Base-64 helpers ---------- */

    public String saltBase64() {
        return Base64.encodeBase64String(salt);
    }

    public String digestBase64() {
        return Base64.encodeBase64String(digest);
    }

    /**
     * Concatenates {@code salt || digest} into one array
     * (handy for the steganography embed step).
     */
    public byte[] concatenated() {
        return ByteUtils.concat(salt, digest);
    }

    /**
     * Human-friendly view useful in quick logs / debugging.
     */
    @Override
    public String toString() {
        return "salt=" + saltHex() + "  digest=" + digestHex();
    }
}
