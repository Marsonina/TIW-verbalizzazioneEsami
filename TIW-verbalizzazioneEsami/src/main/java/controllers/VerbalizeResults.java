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

import beans.ExamStudent;
import beans.User;
import dao.CourseDAO;
import dao.ExamDAO;
import utility.DbConnection;
import utility.Templating;
import beans.Verbal;

@WebServlet("/VerbalizeResults")
public class VerbalizeResults extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
	
	public VerbalizeResults() {
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
		String selectedDate = request.getParameter("examDate");
		String selectedCourse = request.getParameter("courseId");
		List<ExamStudent> students = new ArrayList<ExamStudent>();
		ExamDAO eDao = new ExamDAO(connection, Integer.parseInt(selectedCourse) ,selectedDate);
		Verbal verbal = new Verbal();

		try {
			 
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
		}
		
		
		try {
			CourseDAO cDAO = new CourseDAO(connection, Integer.parseInt(selectedCourse));
			String matricolaTeacher = cDAO.findOwnerTeacher();
			verbal.setMatricolaTeacher(matricolaTeacher);
			try {
				int id = eDao.createVerbal(verbal);
				verbal.setVerbalId(id);
				students = eDao.getVerbalizedResult();
				eDao.verbalize();
				System.out.print(verbal.getVerbalId());

			} catch (SQLException e) {
				try {
			        connection.rollback();
			    } catch (SQLException e1) {
			        e1.printStackTrace();
			    }
			}
			
		} catch (SQLException e) {
			// throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in verbalize results");

		}
		
		String path = "/WEB-INF/VerbalPage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("students", students);
		ctx.setVariable("courseId",selectedCourse);
		ctx.setVariable("examDate", selectedDate);
		ctx.setVariable("verbal", verbal);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}