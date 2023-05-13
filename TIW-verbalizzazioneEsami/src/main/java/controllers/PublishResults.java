package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import beans.Course;
import beans.Exam;
import beans.ExamStudent;
import beans.User;
import dao.CourseDAO;
import dao.ExamDAO;
import dao.TeacherDAO;
import utility.DbConnection;
import utility.Templating;


@WebServlet("/PublishResults")
public class PublishResults extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public PublishResults() {
		super();
	}

	public void init() throws ServletException {
		connection = DbConnection.connect(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String selectedDate = request.getParameter("examDate");
		String selectedCourse = request.getParameter("courseId");
		List<ExamStudent> students = new ArrayList<ExamStudent>();
		ExamDAO eDao = new ExamDAO(connection, Integer.parseInt(selectedCourse) ,selectedDate);

		/*try {
			 
			CourseDAO cDao = new CourseDAO(connection, Integer.parseInt(selectedCourse));
			
			if(cDao.findCourse() == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error with course choice");
				return;
			}
			String currTeacher = cDao.findOwnerTeacher();
			if(currTeacher == null || !currTeacher.equals(user.getMatricola())) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Trying to access non-own course");
				return;
			}
			
			
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in teacher's exams database extraction");
		}
		
		try {
			 
			ExamDAO exDao = new ExamDAO(connection,Integer.parseInt(selectedCourse) ,selectedDate );
			
			if(exDao.findExam() == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error with exam choice");
				return;
			}

			
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in teacher's exams database extraction");
		}*/
		
		
		try {
			eDao.Publish();	
			
		} catch (SQLException e) {
			// throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in enrolled students database extraction");
		}
		
		String path = "/GoToEnrolledStudents";
		request.setAttribute("examDate", selectedDate);
		request.setAttribute("courseId", selectedCourse);
		request.getRequestDispatcher(path).forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
