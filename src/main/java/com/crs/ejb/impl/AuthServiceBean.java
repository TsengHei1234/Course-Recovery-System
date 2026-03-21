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

        // 如果你现在 DB 还是明文密码，先暂时这样：
//        if (!user.getPassword().equals(password)) {
//            return null;
//        }

        // 如果后面换成 hash，再改成：
         if (!PasswordUtil.verifyPassword(password, user.getPassword())) return null;

        return user;
    }
}