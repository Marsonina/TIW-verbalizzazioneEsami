package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import beans.ExamStudent;
import java.util.ArrayList;
import java.util.List;

import beans.Course;
import beans.Exam;
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
		String query = "SELECT matricola, name, surname, degree, email, result, resultState FROM student, exam_students"
				+ " WHERE matricolaStudent = matricola AND matricola = ? AND courseId = ? AND examDate = ?";
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
	
	public List<ExamStudent> getStudents() throws SQLException {
		List<ExamStudent> users = new ArrayList<ExamStudent>();
		String query = "SELECT student.matricola, student.name, student.surname, student.degree, student.email, exam_students.result, exam_students.resultState"
				+ " FROM student, exam_students WHERE matricola = matricolaStudent AND courseId = ? AND examDate = ? ";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseId);
			pstatement.setString(2, chosenDate);
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
	
	public Exam findExam() throws SQLException{
		String query= "SELECT date FROM exam WHERE courseId = ? AND date = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseId);
			pstatement.setString(2, chosenDate);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					return null;
				else {
					result.next();
					Exam exam = new Exam();
					exam.setDate(result.getString("date"));
					return exam;
				}
			}
		}		
	}
	
	public List<String> findExamStudent() throws SQLException{
		List<String> users = new ArrayList<String>();
		String query= "SELECT matricolaStudent FROM exam_students WHERE courseId = ? AND examDate = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseId);
			pstatement.setString(2, chosenDate);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
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