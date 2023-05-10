package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.ExamStudent;

public class ExamDAO {
	private Connection connection;
	private int courseId;
	private String chosenDate;
	
	
	public ExamDAO(Connection connection, int courseId, String chosenDate) {
		this.connection = connection;
		this.courseId = courseId;
		this.chosenDate = chosenDate;
	}
	
	public ExamStudent getResult(String matricola) throws SQLException{
		ExamStudent examStudent = null;
		String query = "SELECT matricola, name, surname, degree, email, result, resultState FROM student, exam_students WHERE matricolaEnrolled = matricola AND matricola = ? AND examCourseId = ? AND examDate = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, matricola);
			pstatement.setInt(2, this.courseId);
			pstatement.setString(3, this.chosenDate);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					examStudent = new ExamStudent();
					examStudent.setMatricola(result.getString("matricola"));
					examStudent.setName(result.getString("name"));
					examStudent.setSurname(result.getString("surname"));
					examStudent.setDegree(result.getString("degree"));
					examStudent.setEmail(result.getString("email"));
					examStudent.setResult(result.getString("result"));
					examStudent.setResultState(result.getString("resultState"));
				}
			}
		}
		return examStudent;
	}
}