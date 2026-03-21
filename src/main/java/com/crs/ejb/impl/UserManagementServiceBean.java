package com.crs.ejb.impl;

import com.crs.dao.RoleDAO;
import com.crs.dao.UserDAO;
import com.crs.ejb.UserManagementService;
import com.crs.model.Role;
import com.crs.model.User;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class UserManagementServiceBean implements UserManagementService {

    @EJB
    private UserDAO userDAO;

    @EJB
    private RoleDAO roleDAO;

    @Override
    public List<User> getAllUsers() {
        return userDAO.findAllUsers();
    }

    @Override
    public User getUserById(int userId) {
        return userDAO.findById(userId);
    }
    
    @Override
    public User getUserByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleDAO.findAllRoles();
    }

    @Override
    public void createUser(User user) {
        user.setStatus("ACTIVE");
        userDAO.insertUser(user);
    }

    @Override
    public void updateUser(User user) {
        userDAO.updateUser(user);
    }

    @Override
    public void updateUserStatus(int userId, String status) {
        userDAO.updateUserStatus(userId, status);
    }
}