//userDAO
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import beans.User;
import beans.Course;

public class CourseDAO {
	private Connection con;
	private int id;
	
	public CourseDAO(Connection connection, int i) {
		this.con = connection;
		this.id = i;
	}

/*	public Course findCourseTeacher() throws SQLException {
		String query = "SELECT courseId, name FROM teacher, course  WHERE matricola = matricolaTeacher AND courseId = ? ";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, id);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					Course course = new Course();
					course.setCourseId(result.getInt("courseId"));
					course.setCourseName(result.getString("courseName"));
					return course;
				}
			}
		}
	}
	public Course findCourseStudent() throws SQLException {
		String query = "SELECT courseId, name FROM student, course_students WHERE mqatricola = matricolaStudent AND courseId = ? ";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, id);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					Course course = new Course();
					course.setCourseId(result.getInt("courseId"));
					course.setCourseName(result.getString("courseName"));
					return course;
				}
			}
		}
	}
	public User findOwnerTeacher() throws SQLException {
		String query = "SELECT teacher.matricola, teacher.name, teacher.surname FROM teacher,course  WHERE matricola = matricolaTeacher courseId= ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, id);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setMatricola(result.getString("matricola"));
					user.setName(result.getString("name"));
					user.setSurname("surname");
					user.setRole("teacher");
					return user;
				}
			}
		}
	}
	
	public User findAttendingStudent() throws SQLException {
		String query = "SELECT student.matricola, student.name, student.surname FROM student,course_students,exam_s  WHERE matricola = matricolaTeacher courseId= ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, id);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setMatricola(result.getString("matricola"));
					user.setName(result.getString("name"));
					user.setSurname("surname");
					user.setRole("student");
					return user;
				}
			}
		}
	}*/
}
