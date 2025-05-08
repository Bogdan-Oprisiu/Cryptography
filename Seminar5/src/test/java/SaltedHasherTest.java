import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

import hash.HashResult;
import hash.SaltedHasher;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for SaltedHasher and HashResult.
 */
class SaltedHasherTest {

    private static final byte[] MSG = "The quick brown fox".getBytes(StandardCharsets.UTF_8);
    private static final SecureRandom RNG = new SecureRandom();

    // --------------------------------------------------------------------- //
    // 1. Digest must always be 32 bytes (SHA-256)                            //
    // --------------------------------------------------------------------- //
    @Test
    void digestHasCorrectLength() {
        SaltedHasher hasher = new SaltedHasher();        // default: 16-byte salt
        HashResult hr = hasher.hash(MSG);

        assertEquals(32, hr.digest().length, "SHA-256 digest should be 32 bytes");
    }

    // --------------------------------------------------------------------- //
    // 2. Salt must have the requested length                                //
    // --------------------------------------------------------------------- //
    @Test
    void saltHasCorrectLength() {
        int saltLen = 16;
        SaltedHasher hasher = new SaltedHasher(saltLen, RNG);
        HashResult hr = hasher.hash(MSG);

        assertEquals(saltLen, hr.salt().length, "Salt length should equal constructor argument");
    }

    // --------------------------------------------------------------------- //
    // 3. Successive hashes of same message must differ (random salt)        //
    // --------------------------------------------------------------------- //
    @Test
    void successiveCallsUseDifferentRandomSalt() {
        SaltedHasher hasher = new SaltedHasher();

        HashResult first  = hasher.hash(MSG);
        HashResult second = hasher.hash(MSG);

        assertFalse(Arrays.equals(first.salt(),   second.salt()),   "Salts should differ");
        assertFalse(Arrays.equals(first.digest(), second.digest()), "Digests should differ when salts differ");
    }

    // --------------------------------------------------------------------- //
    // 4. Hex helpers faithfully encode raw bytes                            //
    // --------------------------------------------------------------------- //
    @Test
    void hexHelpersMatchRawBytes() throws Exception {
        SaltedHasher hasher = new SaltedHasher();
        HashResult hr = hasher.hash(MSG);

        // Each byte → two hex chars
        assertEquals(hr.salt().length * 2,   hr.hexSalt().length());
        assertEquals(hr.digest().length * 2, hr.hexDigest().length());

        // Round-trip (lower-case hex)
        assertArrayEquals(hr.salt(),   Hex.decodeHex(hr.hexSalt()));
        assertArrayEquals(hr.digest(), Hex.decodeHex(hr.hexDigest()));
    }
}
