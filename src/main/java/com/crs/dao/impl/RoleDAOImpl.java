package com.crs.dao.impl;

import com.crs.dao.RoleDAO;
import com.crs.model.Role;
import com.crs.util.DBConnection;
import jakarta.ejb.Stateless;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class RoleDAOImpl implements RoleDAO {

    private static final String FIND_ALL_ROLES_SQL =
            "SELECT role_id, role_name FROM roles ORDER BY role_id";

    @Override
    public List<Role> findAllRoles() {
        List<Role> roleList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_ROLES_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Role role = new Role();
                role.setRoleId(rs.getInt("role_id"));
                role.setRoleName(rs.getString("role_name"));
                roleList.add(role);
            }

        } catch (Exception e) {
            System.out.println("ERROR in findAllRoles()");
            e.printStackTrace();
        }

        return roleList;
    }
}