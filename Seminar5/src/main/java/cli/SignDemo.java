package cli;

import crypto.ElGamalKeyPair;
import crypto.ElGamalSigner;
import crypto.ElGamalVerifier;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public final class SignDemo {
    public static void main(String[] args) {
        var kp = ElGamalKeyPair.fixed();
        var sig = new ElGamalSigner(kp).sign("B".getBytes(StandardCharsets.US_ASCII), new SecureRandom());
        boolean ok = new ElGamalVerifier(kp).verify("B", sig[0], sig[1]);

        System.out.printf("r = %s%n", sig[0]);
        System.out.printf("s = %s%n", sig[1]);
        System.out.println("Verification: " + ok);
    }
}
