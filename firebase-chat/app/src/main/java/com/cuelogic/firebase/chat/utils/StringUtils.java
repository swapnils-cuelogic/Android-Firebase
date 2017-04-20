package com.cuelogic.firebase.chat.utils;

/**
 * Created by Audetemi Inc. on 20/04/17.
 */

public class StringUtils {
    public static boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
