package utility;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.User;
import dao.CourseDAO;
import dao.ExamDAO;

public class CheckPermissions {
	
	User user;
	Connection connection;
	HttpServletRequest request;
	HttpServletResponse response;
	
	public CheckPermissions(Connection connection, User user, 
			HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.connection = connection;
		this.user = user;
	}

	public void checkTeacherPermissions(int chosenCourseId) throws IOException, SQLException {
		CourseDAO cDao = new CourseDAO(connection, chosenCourseId);
		String homePage = request.getServletContext().getContextPath() + "/GoToHomeTeacher";
			//checking if the selected course exists
			if(cDao.findCourse() == null) {
				response.sendRedirect(homePage);
				return;
			}
			//checking if the current teacher owns the selected course
			String currTeacher = cDao.findOwnerTeacher();
			if(currTeacher == null || !currTeacher.equals(user.getMatricola())) {
				response.sendRedirect(homePage);
				return;
			}
		}
	
	public void checkExamDate(ExamDAO eDao) throws SQLException, IOException {
		String homePage = request.getServletContext().getContextPath() + "/GoToHomeTeacher";
		if(eDao.findExam() == null) {
			response.sendRedirect(homePage);
			return;
		}
	}
}
