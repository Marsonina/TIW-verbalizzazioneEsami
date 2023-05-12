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


@WebServlet("/RefuseMark")
public class RefuseMark extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
	
	public RefuseMark() {
		super();
	}

	public void init() throws ServletException {
		templateEngine = Templating.template(getServletContext());
		connection = DbConnection.connect(getServletContext());
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
			eDao.Refuse(user.getMatricola());
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
