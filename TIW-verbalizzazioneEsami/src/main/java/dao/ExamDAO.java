package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.User; 
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
	
	public List<ExamStudent> getStudents() throws SQLException {
		List<ExamStudent> users = new ArrayList<ExamStudent>();
		String query = "SELECT student.matricola, student.name, student.surname, student.degree, student.email, exam_students.result, exam_students.resultState"
				+ " FROM student, exam_students WHERE matricola = matricolaStudent AND courseId = 1 AND examDate = '2023-01-01'";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			//pstatement.setInt(1, courseId);
			//pstatement.setString(2, chosenDate);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					while(result.next()) {
						ExamStudent student = new ExamStudent();
						student.setMatricola(result.getString("matricola"));
						student.setName(result.getString("name"));
						student.setSurname(result.getString("surname"));
						student.setDegree(result.getString("degree"));
						student.setEmail(result.getString("email"));
						student.setResult(result.getString("result"));
						student.setResultState(result.getString("resultState"));
						users.add(student);
					}
				}
			}
		}
		return users;
	}
}