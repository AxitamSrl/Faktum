package com.faktum.util;

import java.security.SecureRandom;

/**
 * Simple CUID-like ID generator compatible with Prisma's cuid() output.
 * Generates 25-char lowercase alphanumeric strings prefixed with 'c'.
 */
public final class CuidGenerator {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int ID_LENGTH = 25;

    private CuidGenerator() {}

    public static String generate() {
        var sb = new StringBuilder(ID_LENGTH);
        sb.append('c');
        for (int i = 1; i < ID_LENGTH; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
