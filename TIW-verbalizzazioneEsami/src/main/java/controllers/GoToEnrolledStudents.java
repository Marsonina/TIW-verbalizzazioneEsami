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

import beans.User;
import utility.DbConnection;
import utility.Templating;
import beans.ExamStudent;
import dao.CourseDAO;
import dao.ExamDAO;

@WebServlet("/GoToEnrolledStudents")
public class GoToEnrolledStudents extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
	
	public GoToEnrolledStudents() {
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
		String order = request.getParameter("order");
		String orderInput = request.getParameter("orderInput");
		boolean checkPublish = false;
		boolean checkVerbalize = false;

		System.out.print(orderInput);
		if(order == null) {
			order = "ASC";
		}else if(order.equals("ASC")) {
			order = "DESC";
		}else if(order.equals("DESC")) {
			order = "ASC";
		}
			
		
		if(orderInput == null) {
			orderInput = "matricolaStudent";
		}
		

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
			String orderBy = orderInput + " " + order;
			System.out.print(orderBy);
			students = eDao.getStudents(orderBy);
			if(students!= null) {
				 checkPublish = false;
				 checkVerbalize = false;
				for (ExamStudent student : students) {
				    if (student.getResultState().equals("INSERITO")) {
				        checkPublish = true;
				        break;
				    }
				}
				for (ExamStudent student : students) {
				    if (student.getResultState().equals("PUBBLICATO") || student.getResultState().equals("RIFIUTATO") ||
				    		student.getResultState().equals("ASSENTE")){
				        checkVerbalize = true;
				        break;
				    }
				}
				System.out.println(checkPublish);
				System.out.println(checkVerbalize);
			}
				
		} catch (SQLException e) {
			// throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in enrolled students database extraction");
		}
		String path = "/WEB-INF/EnrolledStudentsPage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("students", students);
		ctx.setVariable("courseId",selectedCourse);
		ctx.setVariable("examDate", selectedDate);
		ctx.setVariable("order", order);
		ctx.setVariable("orderInput", orderInput);
		ctx.setVariable("checkPublish", checkPublish);
		ctx.setVariable("checkVerbalize", checkVerbalize);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
