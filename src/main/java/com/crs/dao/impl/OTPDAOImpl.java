package com.crs.dao.impl;

import com.crs.dao.OTPDAO;
import com.crs.util.DBConnection;
import jakarta.ejb.Stateless;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Stateless
public class OTPDAOImpl implements OTPDAO {

    private static final String INSERT_SQL =
            "INSERT INTO otps (user_id, otp, expired_time) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 5 MINUTE))";

    private static final String VERIFY_SQL =
            "SELECT * FROM otps WHERE user_id = ? AND otp = ? AND expired_time > NOW() ORDER BY otp_id DESC LIMIT 1";
    
    private static final String DELETE_SQL =
            "DELETE FROM otps WHERE user_id = ? AND otp = ?";
    
    @Override
    public void deleteOTP(int userId, String otp) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {

            ps.setInt(1, userId);
            ps.setString(2, otp);

            int rows = ps.executeUpdate();
            System.out.println("Rows deleted from otps: " + rows);

        } catch (Exception e) {
            System.out.println("ERROR in deleteOTP()");
            e.printStackTrace();
        }
    }

    @Override
    public void saveOTP(int userId, String otp) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

            ps.setInt(1, userId);
            ps.setString(2, otp);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean verifyOTP(int userId, String otp) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(VERIFY_SQL)) {

            ps.setInt(1, userId);
            ps.setString(2, otp);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}