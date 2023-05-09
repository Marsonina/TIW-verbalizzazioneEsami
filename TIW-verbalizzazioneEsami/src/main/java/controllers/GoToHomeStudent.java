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
import beans.User;
import dao.StudentDAO;
import it.polimi.tiw.projects.dao.ProjectDAO;
import utility.DbConnection;
import utility.Templating;



@WebServlet("/GoToHomeStudent")
public class GoToHomeStudent extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public GoToHomeStudent() {
		super();
	}

	public void init() throws ServletException {
		connection = DbConnection.connect(getServletContext());
		templateEngine = Templating.template(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession s = request.getSession();
		User user = (User) s.getAttribute("user");
		String chosenCourse = request.getParameter("courseId");
		StudentDAO sDao = new StudentDAO(connection, user.getMatricola());
		List<Course> courses = new ArrayList<Course>();
		List<Exam> exams = new ArrayList<Exam>();
		int chosenCourseId = 0;
		
		try {
			courses = sDao.getCourses();
			if (chosenCourse != null) { //non ho messo il caso base in cui chosen project è null e vengono semplicemnete visualizzati
				chosenCourseId = Integer.parseInt(chosenCourse);
			}
			/*
			if(sDao.getExamDates() == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error with course choice");
				return;
			}
			User owner = pDao.findOwner();
			if(owner == null || owner.getId() != u.getId()) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Trying to access non-owned project");
				return;
			}
			workers = pDao.findWorkers();
		} catch (SQLException e) {
			// throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in admin's project database extraction");
		}*/

		String path = "/WEB-INF/StudentHomePage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}