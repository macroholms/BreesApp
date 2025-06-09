package com.example.breesapp.classes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class Validator {

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidLength(String value) {
        return value.length() <= 20 && value.length() >= 8;
    }

    public static boolean isValidEmail(String email) {
        String emailPattern = "^[a-z0-9]+@[a-z0-9]+\\.[a-z0-9]+$";
        return Pattern.compile(emailPattern).matcher(email).matches();
    }

    public static boolean doPasswordsMatch(String pass1, String pass2) {
        return pass1.equals(pass2);
    }

    public static boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            Date date = sdf.parse(dateStr);
            return dateStr.equals(sdf.format(date));
        } catch (ParseException e) {
            return false;
        }
    }
}
