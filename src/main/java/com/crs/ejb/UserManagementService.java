package com.crs.ejb;

import com.crs.model.Role;
import com.crs.model.User;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface UserManagementService {
    List<User> getAllUsers();
    User getUserById(int userId);
    User getUserByEmail(String email);
    List<Role> getAllRoles();
    void createUser(User user);
    void updateUser(User user);
    void updateUserStatus(int userId, String status);
}