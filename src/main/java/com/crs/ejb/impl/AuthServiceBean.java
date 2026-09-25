package com.crs.ejb.impl;

import com.crs.dao.UserDAO;
import com.crs.ejb.AuthService;
import com.crs.model.User;
import com.crs.util.PasswordUtil;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class AuthServiceBean implements AuthService {

    @EJB
    private UserDAO userDAO;

    @Override
    public User login(String email, String password) {
        User user = userDAO.findByEmail(email);

        if (user == null) {
            return null;
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            return null;
        }

        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            return null;
        }

        // Upgrade legacy plaintext passwords after the first successful login.
        if (PasswordUtil.needsRehash(user.getPassword())) {
            String hashedPassword = PasswordUtil.hashPassword(password);
            userDAO.updatePassword(user.getUserId(), hashedPassword);
            user.setPassword(hashedPassword);
        }

        return user;
    }
}
