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
import dao.CourseDAO;
import dao.TeacherDAO;
import utility.DbConnection;
import utility.Templating;


@WebServlet("/GoToHomeTeacher")
public class GoToHomeTeacher extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
	
	public GoToHomeTeacher() {
		super();
	}

	public void init() throws ServletException {
		//connecting with DB
		templateEngine = Templating.template(getServletContext());
		//configuring template
		connection = DbConnection.connect(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession s = request.getSession();
		String homePage = request.getServletContext().getContextPath() + "/GoToHomeTeacher";
		
		User user = (User) s.getAttribute("user");
		String chosenCourse = request.getParameter("courseId");
		
		TeacherDAO tDao = new TeacherDAO(connection, user.getMatricola());
		List<Course> courses = new ArrayList<Course>();
		List<Exam> exams = new ArrayList<Exam>();
		int chosenCourseId = 0;
		
		
		try {
			courses = tDao.getCourses();
			if (chosenCourse != null) { 
				if (chosenCourse.isEmpty()) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
					return;
				}else {
					try {
					chosenCourseId = Integer.parseInt(chosenCourse);
					}catch(NumberFormatException e) {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("Bad request, retry!");
						return;
					}
					//exams corresponding to selected course
					exams = tDao.getExamDates(chosenCourseId);
					//check permissions
					CourseDAO cDao = new CourseDAO(connection, chosenCourseId);
					if(cDao.findCourse() == null) {
						response.sendRedirect(homePage);
						return;
					}
					String currTeacher = cDao.findOwnerTeacher();
					if(currTeacher == null || !currTeacher.equals(user.getMatricola())) {
						response.sendRedirect(homePage);
						return;
					}
				}
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failure in course's info database extraction");
		}
		
		String path = "/WEB-INF/TeacherHomePage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("courses", courses);
		ctx.setVariable("courseId", chosenCourseId);
		ctx.setVariable("exams", exams);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
