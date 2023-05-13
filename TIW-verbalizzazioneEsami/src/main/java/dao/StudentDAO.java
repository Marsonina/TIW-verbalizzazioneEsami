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
		String query = "SELECT id, name FROM course, course_students  WHERE courseId = id AND matricolaStudent = ? ORDER BY name DESC";
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
	
	//A method that returns the list of exam sessions for a course in which the student is enrolled for the exam(passed by argument)
	public List<Exam> getExamDates(int chosenCourseId) throws SQLException {
		List<Exam> exams = new ArrayList<Exam>();
		String query = "SELECT examDate FROM exam_students WHERE courseId = ? AND matricolaStudent = ? ORDER BY examDate DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, chosenCourseId);
			pstatement.setString(2,  this.matricola);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Exam exam = new Exam();
					exam.setDate(result.getString("examDate"));
					exams.add(exam);
				}
			}
		}
	return exams;
	}
	
}