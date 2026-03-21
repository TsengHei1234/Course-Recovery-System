package com.crs.dao.impl;

import com.crs.dao.UserDAO;
import com.crs.model.User;
import com.crs.util.DBConnection;
import jakarta.ejb.Stateless;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.*;

@Stateless
public class UserDAOImpl implements UserDAO {

    private static final String FIND_BY_EMAIL_SQL =
            "SELECT u.user_id, u.role_id, r.role_name, u.name, u.email, u.password, u.status " +
            "FROM users u " +
            "JOIN roles r ON u.role_id = r.role_id " +
            "WHERE u.email = ?";
    
    private static final String UPDATE_PASSWORD_SQL =
            "UPDATE users SET password = ? WHERE user_id = ?";
    
    private static final String FIND_ALL_USERS_SQL =
            "SELECT u.user_id, u.role_id, r.role_name, u.name, u.email, u.password, u.status " +
            "FROM users u " +
            "JOIN roles r ON u.role_id = r.role_id " +
            "ORDER BY u.user_id";

    private static final String FIND_BY_ID_SQL =
            "SELECT u.user_id, u.role_id, r.role_name, u.name, u.email, u.password, u.status " +
            "FROM users u " +
            "JOIN roles r ON u.role_id = r.role_id " +
            "WHERE u.user_id = ?";

    private static final String UPDATE_USER_STATUS_SQL =
            "UPDATE users SET status = ? WHERE user_id = ?";
    
    private static final String INSERT_USER_SQL =
            "INSERT INTO users (role_id, name, email, password, status) VALUES (?, ?, ?, ?, ?)";
    
    private static final String UPDATE_USER_SQL =
            "UPDATE users SET role_id = ?, name = ?, email = ?, status = ? WHERE user_id = ?";

    @Override
    public void updatePassword(int userId, String password) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_PASSWORD_SQL)) {

            ps.setString(1, password);
            ps.setInt(2, userId);

            int rows = ps.executeUpdate();
            System.out.println("Rows updated in users: " + rows);

        } catch (Exception e) {
            System.out.println("ERROR in updatePassword()");
            e.printStackTrace();
        }
    }
    
    @Override
    public User findByEmail(String email) {
        User user = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_EMAIL_SQL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setRoleId(rs.getInt("role_id"));
                    user.setRoleName(rs.getString("role_name"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setStatus(rs.getString("status"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }
    
    @Override
    public List<User> findAllUsers() {
        List<User> userList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_USERS_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setStatus(rs.getString("status"));
                userList.add(user);
            }

        } catch (Exception e) {
            System.out.println("ERROR in findAllUsers()");
            e.printStackTrace();
        }

        return userList;
    }

    @Override
    public User findById(int userId) {
        User user = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setRoleId(rs.getInt("role_id"));
                    user.setRoleName(rs.getString("role_name"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setStatus(rs.getString("status"));
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in findById()");
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public void updateUserStatus(int userId, String status) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_USER_STATUS_SQL)) {

            ps.setString(1, status);
            ps.setInt(2, userId);

            int rows = ps.executeUpdate();
            System.out.println("Rows updated in users (status): " + rows);

        } catch (Exception e) {
            System.out.println("ERROR in updateUserStatus()");
            e.printStackTrace();
        }
    }
    
    @Override
    public void insertUser(User user) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_USER_SQL)) {

            ps.setInt(1, user.getRoleId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getStatus());

            int rows = ps.executeUpdate();
            System.out.println("Rows inserted into users: " + rows);

        } catch (Exception e) {
            System.out.println("ERROR in insertUser()");
            e.printStackTrace();
        }
    }
    
    @Override
    public void updateUser(User user) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_USER_SQL)) {

            ps.setInt(1, user.getRoleId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getStatus());
            ps.setInt(5, user.getUserId());

            int rows = ps.executeUpdate();
            System.out.println("Rows updated in users: " + rows);

        } catch (Exception e) {
            System.out.println("ERROR in updateUser()");
            e.printStackTrace();
        }
    }
}