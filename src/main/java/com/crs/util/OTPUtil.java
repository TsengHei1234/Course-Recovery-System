package com.crs.util;

import java.security.SecureRandom;

public class OTPUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String generateOTP() {
        int otp = 100000 + SECURE_RANDOM.nextInt(900000);
        return String.valueOf(otp);
    }
}
