package com.crs.dao.impl;

import com.crs.dao.StudentDAO;
import com.crs.util.DBConnection;

import jakarta.ejb.Stateless;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Stateless
public class StudentDAOImpl implements StudentDAO{
	private static final String UPDATE_STUDENT_TERM_SQL =
	        "UPDATE students SET year_id = ?, semester_id = ? WHERE student_id = ?";

	@Override
	public void updateStudentTerm(String studentId, int yearId, int semesterId) {
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(UPDATE_STUDENT_TERM_SQL)) {

	        ps.setInt(1, yearId);
	        ps.setInt(2, semesterId);
	        ps.setString(3, studentId);

	        ps.executeUpdate();

	    } catch (Exception e) {
	        System.out.println("ERROR in updateStudentTerm()");
	        e.printStackTrace();
	    }
	}
	
	private static final String FIND_STUDENT_EMAIL_SQL =
	        "SELECT email FROM students WHERE student_id = ?";

	private static final String FIND_STUDENT_NAME_SQL =
	        "SELECT student_name FROM students WHERE student_id = ?";

	@Override
	public String findStudentEmailByStudentId(String studentId) {
	    String email = null;

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(FIND_STUDENT_EMAIL_SQL)) {

	        ps.setString(1, studentId);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                email = rs.getString("email");
	            }
	        }

	    } catch (Exception e) {
	        System.out.println("ERROR in findStudentEmailByStudentId()");
	        e.printStackTrace();
	    }

	    return email;
	}

	@Override
	public String findStudentNameByStudentId(String studentId) {
	    String name = null;

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(FIND_STUDENT_NAME_SQL)) {

	        ps.setString(1, studentId);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                name = rs.getString("student_name");
	            }
	        }

	    } catch (Exception e) {
	        System.out.println("ERROR in findStudentNameByStudentId()");
	        e.printStackTrace();
	    }

	    return name;
	}
	
	private static final String FIND_PROGRAM_NAME_SQL =
	        "SELECT p.program_name " +
	        "FROM students s " +
	        "JOIN programs p ON s.program_id = p.program_id " +
	        "WHERE s.student_id = ?";

	@Override
	public String findProgramNameByStudentId(String studentId) {
	    String programName = null;

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(FIND_PROGRAM_NAME_SQL)) {

	        ps.setString(1, studentId);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                programName = rs.getString("program_name");
	            }
	        }

	    } catch (Exception e) {
	        System.out.println("ERROR in findProgramNameByStudentId()");
	        e.printStackTrace();
	    }

	    return programName;
	}
}
