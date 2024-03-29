package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
		String homePage = request.getServletContext().getContextPath() + "/GoToHomeStudent";
		
		User user = (User) s.getAttribute("user");
		String chosenCourse = request.getParameter("courseId");
		String chosenExam = request.getParameter("examDate");
		
		if (chosenCourse == null || chosenCourse.isEmpty() || chosenExam == null || chosenExam.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate.parse(chosenExam, formatter);
        } catch (DateTimeParseException e) {
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Date format error");
			return;
        }
		int chosenCourseId = Integer.parseInt(chosenCourse);
		ExamDAO eDao = new ExamDAO(connection, chosenCourseId, chosenExam);
		ExamStudent examStudent = new ExamStudent();
		
		//check permissions
		try {			 
			CourseDAO cDao = new CourseDAO(connection, chosenCourseId);
			//checking if the course selected exists
			if(cDao.findCourse() == null) {
				response.sendRedirect(homePage);
				return;
			}
			//checking if the current student attends the selected course
			List<String> currStudents = cDao.findAttendingStudent();
			if(currStudents == null || !currStudents.contains(user.getMatricola())) {
				response.sendRedirect(homePage);
				return;
			}				
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in students' courses database extraction");
		}
		//check permissions
		try {	
			//checking if the exam date selected exists		
			if(eDao.findExam() == null) {
				response.sendRedirect(homePage);
				return;
			}
			//checking if the student is enrolled to a specific exam in a specific date
			List<String> examStudents = eDao.findExamStudent();
			if(examStudents == null || !examStudents.contains(user.getMatricola())) {
				response.sendRedirect(homePage);
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
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in students' info database extraction");
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