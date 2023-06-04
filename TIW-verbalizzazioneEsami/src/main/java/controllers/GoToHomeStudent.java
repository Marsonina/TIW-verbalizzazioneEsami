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
import utility.DbConnection;
import utility.Templating;

import dao.CourseDAO;

//servlet that allows displaying the courses in which the student is enrolled for the exam and to choose one of its exam dates.
@WebServlet("/GoToHomeStudent")
public class GoToHomeStudent extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public GoToHomeStudent() {
		super();
	}

	public void init() throws ServletException {
		//connection with DB
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
		
		StudentDAO sDao = new StudentDAO(connection, user.getMatricola());
		List<Course> courses = new ArrayList<Course>();
		List<Exam> exams = new ArrayList<Exam>();
		int chosenCourseId = 0;
		
		try {
			//courses in which the student is enrolled for the exam
			courses = sDao.getCourses();
			
			if (chosenCourse != null) { 
				chosenCourseId = Integer.parseInt(chosenCourse);
				//exam's available dates corresponding to the selected course
				exams = sDao.getExamDates(chosenCourseId);
				CourseDAO cDao = new CourseDAO(connection, chosenCourseId);	
				//checking if the selection of the course is correct
				if(cDao.findCourse() == null) {
					response.sendRedirect(homePage);
					return;
				}
				//checking if the current student is enrolled to the selected course
				List<String> currStudents = cDao.findAttendingStudent();
				if(currStudents == null || !currStudents.contains(user.getMatricola())) {
					response.sendRedirect(homePage);
					return;
				}
			}
		} catch (SQLException e) {
			// throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in students' courses database extraction");
		}

		String path = "/WEB-INF/StudentHomePage.html";
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
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
			System.err.println("Errore durante la chiusura della connessione: " + sqle.getMessage());
		}
	}

}
