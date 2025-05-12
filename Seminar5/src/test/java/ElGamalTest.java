import crypto.ElGamalKeyPair;
import crypto.ElGamalSigner;
import crypto.ElGamalVerifier;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ElGamalKeyPair, ElGamalSigner and ElGamalVerifier.
 */
class ElGamalTest {

    // --------------------------------------------------------------------- //
    // 1.  Sanity check: β must equal α^a mod p                              //
    // --------------------------------------------------------------------- //
    @Test
    void betaIsComputedCorrectly() {
        ElGamalKeyPair kp = ElGamalKeyPair.fixed();
        BigInteger expectedBeta = kp.alpha().modPow(kp.a(), kp.p());
        assertEquals(expectedBeta, kp.beta(), "β should equal α^a mod p");
    }

    // --------------------------------------------------------------------- //
    // 2.  Round-trip: sign then verify on the same message must be TRUE     //
    // --------------------------------------------------------------------- //
    @Test
    void signAndVerifySucceeds() {
        ElGamalKeyPair kp = ElGamalKeyPair.fixed();
        ElGamalSigner signer   = new ElGamalSigner(kp);
        ElGamalVerifier verifier = new ElGamalVerifier(kp);

        byte[] msg = "B".getBytes(StandardCharsets.US_ASCII);
        BigInteger[] rs = signer.sign(msg, new SecureRandom());

        assertTrue(verifier.verify(msg, rs[0], rs[1]), "Fresh signature should verify");
        // quick range sanity
        assertAll(
                () -> assertTrue(rs[0].signum() > 0 && rs[0].compareTo(kp.p()) < 0, "r in (0,p)"),
                () -> assertTrue(rs[1].signum() > 0 && rs[1].compareTo(kp.p().subtract(BigInteger.ONE)) < 0, "s in (0,p-1)")
        );
    }

    // --------------------------------------------------------------------- //
    // 3.  Tampered message should FAIL                                      //
    // --------------------------------------------------------------------- //
    @Test
    void modifiedMessageFailsVerification() {
        ElGamalKeyPair kp = ElGamalKeyPair.fixed();
        ElGamalSigner signer   = new ElGamalSigner(kp);
        ElGamalVerifier verifier = new ElGamalVerifier(kp);

        BigInteger[] rs = signer.sign("B".getBytes(StandardCharsets.US_ASCII), new SecureRandom());
        assertFalse(verifier.verify("C".getBytes(StandardCharsets.US_ASCII), rs[0], rs[1]),
                "Changing the message must invalidate the signature");
    }

    // --------------------------------------------------------------------- //
    // 4.  Tampered signature (s + 1) should FAIL                            //
    // --------------------------------------------------------------------- //
    @Test
    void modifiedSignatureFailsVerification() {
        ElGamalKeyPair kp = ElGamalKeyPair.fixed();
        ElGamalSigner signer   = new ElGamalSigner(kp);
        ElGamalVerifier verifier = new ElGamalVerifier(kp);

        byte[] msg = "B".getBytes(StandardCharsets.US_ASCII);
        BigInteger[] rs = signer.sign(msg, new SecureRandom());
        BigInteger r = rs[0];
        BigInteger sPlus1 = rs[1].add(BigInteger.ONE).mod(kp.p().subtract(BigInteger.ONE));

        assertFalse(verifier.verify(msg, r, sPlus1),
                "Altering s should make verification fail");
    }
}
