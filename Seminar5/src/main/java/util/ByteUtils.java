package util;

import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;

/**
 * Assorted static helpers for everyday byte-array chores: concatenation,
 * slicing, hex encode/decode, XOR, constant-time equality.
 *
 * <p>Why bother?</p>
 * <ul>
 *   <li>Prevents every class (SaltedHasher, LSBSteganography, etc.) from
 *       re-implementing the same glue code.</li>
 *   <li>Gives you one audited place for tricky bits (e.g. constant-time compare)
 *       instead of sprinkling them around.</li>
 *   <li>Keeps algorithm classes shorter and unit-testable in isolation.</li>
 * </ul>
 */
public final class ByteUtils {

    private ByteUtils() {
    } // utility class; no instances

    /* ------------------------------------------------------------------ */
    /* Concatenation helpers                                              */
    /* ------------------------------------------------------------------ */

    /**
     * Concatenates two byte arrays.
     */
    public static byte[] concat(byte[] a, byte[] b) {
        byte[] out = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }

    /**
     * Variadic version: concat(a, b, c, …).
     */
    public static byte[] concat(byte[]... parts) {
        int len = 0;
        for (byte[] p : parts) len += p.length;
        byte[] out = new byte[len];
        int pos = 0;
        for (byte[] p : parts) {
            System.arraycopy(p, 0, out, pos, p.length);
            pos += p.length;
        }
        return out;
    }

    /* ------------------------------------------------------------------ */
    /* Hex helpers                                                         */
    /* ------------------------------------------------------------------ */

    public static String toHex(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }

    public static byte[] fromHex(String hexLowerCase) {
        try {
            return Hex.decodeHex(hexLowerCase);
        } catch (org.apache.commons.codec.DecoderException e) {
            throw new IllegalArgumentException("Invalid hex input", e);
        }
    }

    /* ------------------------------------------------------------------ */
    /* XOR and slicing helpers (useful in stego or future tasks)          */
    /* ------------------------------------------------------------------ */

    /**
     * Returns <code>a[i] ⊕ b[i]</code> for the length of the shorter array.
     */
    public static byte[] xor(byte[] a, byte[] b) {
        int n = Math.min(a.length, b.length);
        byte[] out = new byte[n];
        for (int i = 0; i < n; i++) out[i] = (byte) (a[i] ^ b[i]);
        return out;
    }

    /**
     * Safe slice: returns a copy of {@code src[offset .. offset+len-1]}.
     */
    public static byte[] slice(byte[] src, int offset, int len) {
        if (offset < 0 || len < 0 || offset + len > src.length)
            throw new IndexOutOfBoundsException("Invalid slice range");
        return Arrays.copyOfRange(src, offset, offset + len);
    }

    /* ------------------------------------------------------------------ */
    /* Constant-time equality (avoids timing side-channels)               */
    /* ------------------------------------------------------------------ */

    public static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int diff = 0;
        for (int i = 0; i < a.length; i++) diff |= a[i] ^ b[i];
        return diff == 0;
    }
}
