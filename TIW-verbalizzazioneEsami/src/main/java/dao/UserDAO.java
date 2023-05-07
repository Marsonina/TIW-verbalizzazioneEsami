//userDAO
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import beans.User;

public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}

	public User checkCredentialsTeacher(String usrn, String pwd) throws SQLException {
		String query = "SELECT  matricola FROM teacher  WHERE username = ? AND password =? ";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setMatricola(result.getInt("id"));
					user.setRole(result.getString("teacher"));
					return user;
				}
			}
		}
	}
	public User checkCredentialsStudent(String usrn, String pwd) throws SQLException {
		String query = "SELECT  matricola FROM student  WHERE username = ? AND password =? ";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setMatricola(result.getInt("id"));
					user.setRole(result.getString("student"));
					return user;
				}
			}
		}
	}
}
