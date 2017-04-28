package com.cuelogic.firebase.chat.utils;

/**
 * Created by Audetemi Inc. on 20/04/17.
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
}
