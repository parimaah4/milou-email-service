package com.milou.util;

import java.util.Random;

public class CodeGenerator {
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static Random random = new Random();

    public static String generateCode() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
