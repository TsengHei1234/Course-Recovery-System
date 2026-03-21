package com.crs.dao;

public interface OTPDAO {
    void saveOTP(int userId, String otp);
    boolean verifyOTP(int userId, String otp);
    void deleteOTP(int userId, String otp);
}