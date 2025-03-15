package org.example.assignment3;

public class Utils {
    // Calculate the greatest common divisor (Euclidean algorithm).
    public static int gcd(int x, int y) {
        if (y == 0) {
            return x;
        }
        return gcd(y, x % y);
    }

    // Compute the modular inverse of a modulo m by brute force.
    public static int modInverse(int a, int m) {
        a = a % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        return -1;
    }
}
