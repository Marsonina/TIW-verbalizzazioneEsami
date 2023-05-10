package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import beans.Course;
import beans.Exam;

public class TeacherDAO {
	private Connection connection;
	private String matricola;

	public TeacherDAO(Connection connection, String matricola) {
		this.connection = connection;
		this.matricola = matricola;
	}

	public List<Course> getCourses() throws SQLException {
		List<Course> courses = new ArrayList<Course>();
		String query = "SELECT id, name FROM course WHERE matricolaTeacher = ? ORDER BY name DESC";
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
	
	public List<Exam> getExamDates(int chosenCourseId) throws SQLException {
		List<Exam> exams = new ArrayList<Exam>();
		String query = "SELECT date FROM course, exam WHERE courseId = id AND courseId = ? AND matricolaTeacher = ? ORDER BY date DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, chosenCourseId);
			pstatement.setString(2,  this.matricola);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Exam exam = new Exam();
					exam.setDate(result.getString("date"));
					exams.add(exam);
				}
			}
		}
	return exams;
	}
	
}