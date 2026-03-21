package com.crs.util;

public class PasswordUtil {

    public static boolean verifyPassword(String inputPassword, String dbPassword) {
        return inputPassword != null && inputPassword.equals(dbPassword);
    }
}