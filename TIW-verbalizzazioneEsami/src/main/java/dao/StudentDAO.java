package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import beans.Course;
import beans.Exam;

public class StudentDAO {
	private Connection connection;
	private String matricola;

	public StudentDAO(Connection connection, String matricola) {
		this.connection = connection;
		this.matricola = matricola;
	}

	public List<Course> getCourses() throws SQLException {
		List<Course> courses = new ArrayList<Course>();
		String query = "SELECT id, name FROM course JOIN course_students  WHERE courseId = id, matricolaStudent = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, this.matricola);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Course course = new Course();
					course.setCourseId(result.getInt("id"));
					course.setCourseName(result.getString("name"));
					courses.add(course);
				}
			}
		}
	return courses;
	}
	
	public List<Exam> getExamDates(String chosenCourse) throws SQLException {
		List<Exam> exams = new ArrayList<Exam>();
		String query = "SELECT date FROM exam_date JOIN course WHERE courseId = id, id = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, chosenCourse);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Course course = new Course();
					course.setCourseId(result.getInt("id"));
					course.setCourseName(result.getString("name"));
					courses.add(course);
				}
			}
		}
	return courses;
	}
	
}