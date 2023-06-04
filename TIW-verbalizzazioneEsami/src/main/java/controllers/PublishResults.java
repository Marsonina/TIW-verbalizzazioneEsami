package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.User;
import dao.CourseDAO;
import dao.ExamDAO;
import utility.DbConnection;


@WebServlet("/PublishResults")
public class PublishResults extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public PublishResults() {
		super();
	}

	public void init() throws ServletException {
		//configuring template
		connection = DbConnection.connect(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession s = request.getSession();
		String homePage = request.getServletContext().getContextPath() + "/GoToHomeTeacher";
		
		User user = (User) s.getAttribute("user");
		String selectedDate = request.getParameter("examDate");
		String selectedCourse = request.getParameter("courseId");
		int chosenCourseId = Integer.parseInt(selectedCourse);
		
		ExamDAO eDao = new ExamDAO(connection, chosenCourseId,selectedDate);
		
		//check permissions
		try { 
			//checking if the selected course exists
			CourseDAO cDao = new CourseDAO(connection, chosenCourseId);
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
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in teacher's exams database extraction");
			return;
		}
		
		try {
			eDao.publish();	
		} catch (SQLException e) {
			// throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failure in database publishment updating");
			return;
		}
		
		String path = "/GoToEnrolledStudents";
		request.setAttribute("examDate", selectedDate);
		request.setAttribute("courseId", selectedCourse);
		request.getRequestDispatcher(path).forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
