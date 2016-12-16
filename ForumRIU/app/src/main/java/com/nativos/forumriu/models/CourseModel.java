package com.nativos.forumriu.models;

import static android.R.attr.name;

/**
 * Created by Jordan on 25-Nov-16.
 */

public class CourseModel {

   private int courseId;
    private String courseName;
    private String courseTeacher;
    private String courseSchedule;
    private String courseClassroom;
    private String courseCode;

    public CourseModel() {
    }

    public CourseModel(String courseClassroom, int courseId, String courseName, String courseSchedule, String courseTeacher) {
        this.courseClassroom = courseClassroom;
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseSchedule = courseSchedule;
        this.courseTeacher = courseTeacher;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseClassroom() {
        return courseClassroom;
    }

    public void setCourseClassroom(String courseClassroom) {
        this.courseClassroom = courseClassroom;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseSchedule() {
        return courseSchedule;
    }

    public void setCourseSchedule(String courseSchedule) {
        this.courseSchedule = courseSchedule;
    }

    public String getCourseTeacher() {
        return courseTeacher;
    }

    public void setCourseTeacher(String courseTeacher) {
        this.courseTeacher = courseTeacher;
    }
}
