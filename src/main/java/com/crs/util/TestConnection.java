package com.crs.util;
import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {

        try {
            Connection conn = DBConnection.getConnection();

            if (conn != null) {
                System.out.println("SUCCESS");
            } else {
                System.out.println("FAILED");
            }

        } catch (SQLException e) {
            System.out.println("ERROR CONNECTING TO DATABASE");
            e.printStackTrace();
        }
    }
}
