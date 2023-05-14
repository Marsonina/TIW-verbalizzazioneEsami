package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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

import beans.ExamStudent;
import beans.User;
import utility.DbConnection;
import utility.Templating;
import dao.CourseDAO;
import dao.ExamDAO;

@WebServlet("/ExamResult")
public class ExamResult extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public ExamResult() {
		super();
	}

	public void init() throws ServletException {
		//connecting with DB
		connection = DbConnection.connect(getServletContext());
		//configuring template
		templateEngine = Templating.template(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession s = request.getSession();
		
		User user = (User) s.getAttribute("user");
		String chosenCourse = request.getParameter("courseId");
		int chosenCourseId = Integer.parseInt(chosenCourse);
		String chosenExam = request.getParameter("examDate");
		
		ExamDAO eDao = new ExamDAO(connection, chosenCourseId, chosenExam);
		ExamStudent examStudent = new ExamStudent();
		
		try {			 
			CourseDAO cDao = new CourseDAO(connection, Integer.parseInt(chosenCourse));
			//checking if the course selected exists
			if(cDao.findCourse() == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error with course choice");
				return;
			}
			//checking if the current student attends the selected course
			List<String> currStudents = cDao.findAttendingStudent();
			if(currStudents == null || !currStudents.contains(user.getMatricola())) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Trying to access non-attended course");
				return;
			}				
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in student's exams database extraction");
		}
		
		try {	
			//checking if the exam date selected exists
			ExamDAO exDao = new ExamDAO(connection,chosenCourseId ,chosenExam );		
			if(exDao.findExam() == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error with exam choice");
				return;
			}
			//checking if the student is enrolled to a specific exam in a specific date
			List<String> examStudents = exDao.findExamStudent();
			if(examStudents == null || !examStudents.contains(user.getMatricola())) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Trying to access non-attended exam");
				return;
			}		
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in student's exams database extraction");
		}
		
		
		try {
			//we obtain all the relevant infos about the student enrolled to the exam 
			examStudent = eDao.getResult(user.getMatricola());
		} catch (SQLException e) {
			// throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in students's infos database extraction");
		}

		String path = "/WEB-INF/ExamResultPage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("examStudent", examStudent);
		ctx.setVariable("courseId", chosenCourseId);
		ctx.setVariable("examDate", chosenExam);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}