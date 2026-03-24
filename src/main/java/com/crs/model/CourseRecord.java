package com.crs.model;

import java.io.Serializable;

public class CourseRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private int courseId;
    private String courseCode;
    private String courseName;
    private int creditHour;
    private String assessmentComponents;

    public CourseRecord() {
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCreditHour() {
        return creditHour;
    }

    public void setCreditHour(int creditHour) {
        this.creditHour = creditHour;
    }

    public String getAssessmentComponents() {
        return assessmentComponents;
    }

    public void setAssessmentComponents(String assessmentComponents) {
        this.assessmentComponents = assessmentComponents;
    }
}
