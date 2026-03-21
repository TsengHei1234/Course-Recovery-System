package com.crs.dao;

import com.crs.model.User;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface UserDAO {
    User findByEmail(String email);
    void updatePassword(int userId, String password);

    List<User> findAllUsers();
    User findById(int userId);
    void updateUserStatus(int userId, String status);
    
    void insertUser(User user);
    void updateUser(User user);
}