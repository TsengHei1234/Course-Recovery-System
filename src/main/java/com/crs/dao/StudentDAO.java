package com.crs.dao;

import jakarta.ejb.Local;

@Local
public interface StudentDAO {
	void updateStudentTerm(String studentId, int yearId, int semesterId);
	
	String findStudentEmailByStudentId(String studentId);
	String findStudentNameByStudentId(String studentId);
	
	String findProgramNameByStudentId(String studentId);
}
