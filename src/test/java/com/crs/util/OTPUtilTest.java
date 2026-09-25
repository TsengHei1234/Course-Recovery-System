package com.crs.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OTPUtilTest {

    @Test
    void generatesSixDigitCodes() {
        for (int index = 0; index < 100; index++) {
            assertTrue(OTPUtil.generateOTP().matches("[0-9]{6}"));
        }
    }
}
