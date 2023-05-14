package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import beans.ExamStudent;
import java.util.ArrayList;
import java.util.List;

import beans.Exam;
import beans.Verbal;
public class ExamDAO {
	private Connection connection;
	private int courseId;
	private String chosenDate;
	
	
	public ExamDAO(Connection connection, int courseId, String chosenDate) {
		this.connection = connection;
		this.courseId = courseId;
		this.chosenDate = chosenDate;
	}
	
	//method that returns all the infos about the student enrolled to a specific exam
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
	
	public List<ExamStudent> getStudents(String orderby) throws SQLException {
	    List<ExamStudent> users = new ArrayList<ExamStudent>();
	    String defaultOrder = "name"; 
	    String orderDirection = "ASC"; 
	    if (orderby != null && !orderby.isEmpty()) {
	        String[] orderParts = orderby.split(" ");
	        defaultOrder = orderParts[0];
	        if (orderParts.length > 1) {
	            orderDirection = orderParts[1];
	        }
	    }

	    String query = "SELECT student.matricola, student.name, student.surname, student.degree, student.email, exam_students.result, exam_students.resultState "
	            + "FROM student, exam_students "
	            + "WHERE matricola = matricolaStudent AND courseId = ? AND examDate = ? "
	            + "ORDER BY ";
	    switch (defaultOrder) {
	        case "name":
	            query += "name " + orderDirection + ", surname " + orderDirection + ", matricolaStudent " + orderDirection;
	            break;
	        case "surname":
	            query += "surname " + orderDirection + ", name " + orderDirection + ", matricolaStudent " + orderDirection;
	            break;
	        case "matricolaStudent":
	            query += "matricolaStudent " + orderDirection + ", name " + orderDirection + ", surname " + orderDirection;
	            break;
	        case "degree":
	            query += "degree " + orderDirection + ", name " + orderDirection + ", surname " + orderDirection;
	            break;
	        case "result":
	            query += "result " + orderDirection + ", name " + orderDirection + ", surname " + orderDirection;
	            break;
	        default:
	            query += "name " + orderDirection + ", surname " + orderDirection + ", matricolaStudent " + orderDirection;
	            break;
	    }

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
	
	//method that find all students enrolled to a specific exam in a specific date
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
	
	public void changeMark(String matricola, String mark) throws SQLException {
		String query= "UPDATE exam_students SET result = ?, resultState = 'INSERITO' "
				+ "WHERE matricolaStudent = ? AND courseId = ? AND examDate = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, mark);
			pstatement.setString(2, matricola);
			pstatement.setInt(3, courseId);
			pstatement.setString(4, chosenDate);
			pstatement.executeUpdate();
		}
	}

	public void Publish() throws SQLException {
		String query = "UPDATE exam_students " +
                "SET resultState = 'PUBBLICATO' " +
                "WHERE resultState = 'INSERITO'";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.executeUpdate();
		}
	}
	
	//method that insert a new verbal into the verbal table in DB and return the id of the verbal
	public int createVerbal(Verbal verbal) throws SQLException {
	    String query = "INSERT INTO verbal(examDate, courseId, dateTime, matricolaTeacher) VALUES (?, ?, ?, ?)";
	    try (PreparedStatement pstatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
	        pstatement.setString(1, chosenDate);
	        pstatement.setInt(2, courseId);
	        pstatement.setTimestamp(3, Timestamp.valueOf(verbal.getDateTime()));
	        pstatement.setString(4, verbal.getMatricolaTeacher());
	        System.out.println(chosenDate);
	        System.out.println(courseId);
	        System.out.println(Timestamp.valueOf(verbal.getDateTime()));
	        System.out.println(verbal.getMatricolaTeacher());
	        pstatement.executeUpdate();
	        
	        try (ResultSet generatedKeys = pstatement.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                return generatedKeys.getInt(1);
	            } else {
	                throw new SQLException("Creating verbal failed, no ID obtained.");
	            }
	        }
	    }
	}
	
	public void verbalize() throws SQLException {
		String query = "UPDATE exam_students " +
                "SET resultState = 'VERBALIZZATO' " +
                "WHERE resultState = 'PUBBLICATO' OR resultState = 'RIFIUTATO'";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.executeUpdate();
		}
	}
		public void Refuse(String matricola) throws SQLException {
		String query = "UPDATE exam_students " +
                "SET resultState = 'RIFIUTATO', result = 'RIMANDATO' " +
                "WHERE resultState = 'PUBBLICATO' AND matricolaStudent= ? AND courseId = ? AND examDate = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, matricola);
			pstatement.setString(3, chosenDate);
			pstatement.setInt(2, courseId);
			pstatement.executeUpdate();
		}
	}
	public List<ExamStudent> getVerbalizedResult() throws SQLException {
		List<ExamStudent> users = new ArrayList<ExamStudent>();
		String query = "SELECT student.matricola, student.name, student.surname, student.degree, student.email, "
				+ "exam_students.result, exam_students.resultState FROM student, exam_students WHERE matricola = matricolaStudent "
				+ "AND courseId = ? AND examDate = ? AND (resultState = 'PUBBLICATO' OR resultState = 'RIFIUTATO' ) ";
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
	
}
