package com.cuelogic.firebase.chat.utils;

import java.util.Random;

/**
 * Created by Harshal Vibhandik on 20/04/17.
 */

public class StringUtils {
    public static boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public static boolean isCuelogicEmail(String email) {
        return email.endsWith("cuelogic.co.in") || email.endsWith("cuelogic.com");
    }
    public static boolean isNotEmptyNotNull(String string) {
        return string != null && !"".equals(string);
    }

    private static final int TOKEN_LENGTH = 32;
    private static final String TOKEN_CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String generateToken() {
        char[] chars = TOKEN_CHARACTERS.toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }
}
