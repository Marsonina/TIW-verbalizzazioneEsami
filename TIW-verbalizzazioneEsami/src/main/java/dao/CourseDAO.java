//userDAO
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Course;

public class CourseDAO {
	private Connection con;
	private int id;
	
	public CourseDAO(Connection connection, int i) {
		this.con = connection;
		this.id = i;
	}

	//method that find the name of a selected course
	public Course findCourse() throws SQLException {
		String query = "SELECT id, name FROM course WHERE id = ? ";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, id);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) 
					return null;
				else {
					result.next();
					Course course = new Course();
					course.setCourseId(result.getInt("id"));
					course.setCourseName(result.getString("name"));
					return course;
				}
			}
		}
	}
	
	//method that find the teacher who holds a specific course
	public String findOwnerTeacher() throws SQLException {
		String query = "SELECT matricolaTeacher FROM course WHERE id= ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, id);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					return result.getString("matricolaTeacher");
				}
			}
		}
	}

	//method that find all the students that are enrolled to a specific course
	public List<String> findAttendingStudent() throws SQLException {
		List<String> users = new ArrayList<String>();
		String query = "SELECT matricolaStudent FROM course_students WHERE courseId= ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, id);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					while(result.next()) {
						users.add(result.getString("matricolaStudent"));
					}
				}
			}
		}
		return users;
	}
}
