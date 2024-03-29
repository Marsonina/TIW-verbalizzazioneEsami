package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

@WebServlet("/GoToModifyPage")
public class GoToModifyPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public GoToModifyPage() {
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
		String homePage = request.getServletContext().getContextPath() + "/GoToHomeTeacher";
		
		User user = (User) s.getAttribute("user");
		String chosenCourse = request.getParameter("courseId");
		String chosenExam = request.getParameter("examDate");
		String matricolaExam = request.getParameter("matricola");
		
		if (chosenCourse == null || chosenCourse.isEmpty() || chosenExam == null || chosenExam.isEmpty() || matricolaExam == null || matricolaExam.isEmpty()) {
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
			//checking if the selected course exists
			if(cDao.findCourse() == null) {
				response.sendRedirect(homePage);
				return;
			}
			String currTeacher = cDao.findOwnerTeacher();
			//checking if the current teacher owns the selected course
			if(currTeacher == null || !currTeacher.equals(user.getMatricola())) {
				response.sendRedirect(homePage);
				return;
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in teacher's exams database extraction");
		}
		//check permissions
		try { 
			//checking if the the exam date is correct
			if(eDao.findExam() == null) {
				response.sendRedirect(homePage);
				return;
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in teacher's exams database extraction");
		}
		
		try {
			ExamStudent examStud = new ExamStudent();
			examStud = eDao.getResult(matricolaExam);
			if(examStud == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The selected student doesn't exist");
				return;
			}
			//checking if the mark is already published or verbalized
			if((examStud.getResultState()).equals("PUBBLICATO")|| (examStud.getResultState()).equals("VERBALIZZATO")) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Trying to access to a published or verbalized exam");
				return;
			}
		}catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in student's exams database updating");
		}
		
		try {
			examStudent = eDao.getResult(matricolaExam);
			if(examStudent == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The student isn't correct");
				return;
			}
			//checking if the mark is already published or verbalized
			if((examStudent.getResultState()).equals("PUBBLICATO")|| (examStudent.getResultState()).equals("VERBALIZZATO")) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Trying to access to a published or verbalized exam");
				return;
			}
		}catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failure in enrolled students database extraction");
		}

		String path = "/WEB-INF/ModifyMarkPage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("examStudent", examStudent);
		ctx.setVariable("examDate", chosenExam);
		ctx.setVariable("matricola", matricolaExam);
		ctx.setVariable("courseId", chosenCourseId);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}