package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.ExamStudent;
import beans.User;
import it.polimi.tiw.projects.beans.Project; 

public class ExamDAO {
	private Connection connection;
	private int courseId;
	private String chosenDate;
	
	
	public ExamDAO(Connection connection, int courseId, String chosenDate) {
		this.connection = connection;
		this.courseId = courseId;
		this.chosenDate = chosenDate;
	}
	
	public ExamStudent getResult() throws SQLException{
		ExamStudent examStudent = null;
		String query = "SELECT matricola, name, surname, degree, email, result, resultState FROM student, exam_students WHERE matricolaEnrolled = matricola AND examCourseId = ? AND examDate = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, this.courseId);
			pstatement.setString(2,  chosenDate);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					examStudent = new ExamStudent();
					examStudent.setMatricola(result.getString("matricola"));
					examStudent.setName(result.getString("name"));
					examStudent.setSurname(result.getString("surname"));
					
				}
			}
		}
		return examStudent;
	}
}