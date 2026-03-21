package com.crs.ejb;

import com.crs.model.User;
import jakarta.ejb.Local;

@Local
public interface AuthService {
    User login(String email, String password);
}