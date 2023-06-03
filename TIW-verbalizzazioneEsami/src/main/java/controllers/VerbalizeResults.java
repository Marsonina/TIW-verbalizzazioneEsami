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
		//connecting with DB
		connection = DbConnection.connect(getServletContext());
		//configuring template
		templateEngine = Templating.template(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession s = request.getSession();
		
		User user = (User) s.getAttribute("user");
		String selectedDate = request.getParameter("examDate");
		String selectedCourse = request.getParameter("courseId");
		int chosenCourseId = Integer.parseInt(selectedCourse);
		
		List<ExamStudent> students = new ArrayList<ExamStudent>();
		ExamDAO eDao = new ExamDAO(connection, chosenCourseId ,selectedDate);
		Verbal verbal = new Verbal();

		boolean checkVerbalize = false;
		
		//check permissions
		try {
			//checking if the selected course exists
			CourseDAO cDao = new CourseDAO(connection, Integer.parseInt(selectedCourse));
			if(cDao.findCourse() == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error with course choice");
				return;
			}
			//checking if the current teacher owns the selected course
			String currTeacher = cDao.findOwnerTeacher();
			if(currTeacher == null || !currTeacher.equals(user.getMatricola())) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Trying to access non-owned course");
				return;
			}	
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in teacher's exams database extraction");
		}
		//check permissions
		try {
			//checking if the the exam date is correct
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
				students = eDao.getVerbalizedResult();
				if(students == null) {
					response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "No verbalizable results");
				}else{
				
					for (ExamStudent student : students) {
					    if (student.getResultState().equals("PUBBLICATO") || student.getResultState().equals("RIFIUTATO") ||
					    		student.getResultState().equals("ASSENTE")){
					        checkVerbalize = true;
					        break;
					    }
					}
					
					if(!checkVerbalize) {
						response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "No verbalizable results");
					}
				}
				int id = eDao.createVerbal(verbal);
				verbal.setVerbalId(id);
				eDao.verbalize();

			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in enrolled exam students info database extraction");
				try {
			        connection.rollback();
			    } catch (SQLException e1) {
			        e1.printStackTrace();
			    }
			}	
		} catch (SQLException e) {
			// throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in course info database extraction");

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
