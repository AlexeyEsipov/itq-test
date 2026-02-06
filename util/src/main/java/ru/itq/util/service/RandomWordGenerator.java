package ru.itq.util.service;

import java.security.SecureRandom;


public class RandomWordGenerator {
    private static final String LETTERS = "ABCDEFGHJKMNPQRSTUVWXYZ";

    private static final String ALL_SYMBOLS =
            "abcdefghijkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789";

    private RandomWordGenerator() {
    }


    public static String generateRandomWord() {
        SecureRandom random = new SecureRandom();
        StringBuilder word = new StringBuilder(10);
        char firstChar = LETTERS.charAt(random.nextInt(LETTERS.length()));
        word.append(firstChar);
        for (int i = 0; i < 9; i++) {
            char nextChar = ALL_SYMBOLS.charAt(random.nextInt(ALL_SYMBOLS.length()));
            word.append(nextChar);
        }
        return word.toString();
    }
}
