package com.crs.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordUtilTest {

    @Test
    void hashesAndVerifiesPasswords() {
        String hash = PasswordUtil.hashPassword("correct horse battery staple");

        assertNotEquals("correct horse battery staple", hash);
        assertTrue(PasswordUtil.verifyPassword("correct horse battery staple", hash));
        assertFalse(PasswordUtil.verifyPassword("wrong password", hash));
        assertFalse(PasswordUtil.needsRehash(hash));
    }

    @Test
    void recognisesLegacyPasswordForMigration() {
        assertTrue(PasswordUtil.verifyPassword("legacy-password", "legacy-password"));
        assertTrue(PasswordUtil.needsRehash("legacy-password"));
    }
}
