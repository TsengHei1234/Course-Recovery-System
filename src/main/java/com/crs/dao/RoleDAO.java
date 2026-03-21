package com.crs.dao;

import com.crs.model.Role;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface RoleDAO {
    List<Role> findAllRoles();
}