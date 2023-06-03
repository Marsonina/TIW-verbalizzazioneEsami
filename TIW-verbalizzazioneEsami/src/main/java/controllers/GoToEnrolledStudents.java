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
		//connecting with DB
		templateEngine = Templating.template(getServletContext());
		//configuring template
		connection = DbConnection.connect(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		boolean checkPublish = false;
		boolean checkVerbalize = false;
		
		HttpSession s = request.getSession();
		String homePage = request.getServletContext().getContextPath() + "/GoToHomeTeacher";
		
		User user = (User) s.getAttribute("user");
		String selectedDate = request.getParameter("examDate");
		String selectedCourse = request.getParameter("courseId");
		int chosenCourseId = Integer.parseInt(selectedCourse);
		String order = request.getParameter("order");
		String orderInput = request.getParameter("orderInput");
		
		//checking missing parameters
		/*if (selectedDate == null || selectedDate.isEmpty()
				|| selectedCourse == null || selectedCourse.isEmpty()
				|| order == null || order.isEmpty()
				|| orderInput == null || orderInput.isEmpty()) {
			response.sendRedirect(homePage);
            return;
        }*/
		
		List<ExamStudent> students = new ArrayList<ExamStudent>();
		ExamDAO eDao = new ExamDAO(connection, chosenCourseId ,selectedDate);

		//we set the order variable depending on the previous 
		//value in order to invert the current order of variables
		if(order == null) {
			order = "ASC";
		}else if(order.equals("ASC")) {
			order = "DESC";
		}else if(order.equals("DESC")) {
			order = "ASC";
		}		
		//we set a default order 
		if(orderInput == null) {
			orderInput = "matricolaStudent";
		}else if(orderInput.equals("result")) {
			
		}
		
		//check permissions
		try {
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
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in teacher's database extraction");
		}
		//check permissions
		try { 
			ExamDAO exDao = new ExamDAO(connection, chosenCourseId,selectedDate );
			if(exDao.findExam() == null) {
				response.sendRedirect(homePage);
				return;
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in teacher's exams database extraction");
		}
		
		try {
			String orderBy = orderInput + " " + order;
			//we obtain the students orderd by the order specified by the client
			students = eDao.getStudents(orderBy);
			if(students!= null) {
				 checkPublish = false;
				 checkVerbalize = false;
				 //we iterate trough the students to check for which students is possible to publish their marks
				for (ExamStudent student : students) {
				    if (student.getResultState().equals("INSERITO")) {
				        checkPublish = true;
				        break;
				    }
				}
				//we iterate trough the students to check for which students is not possible to verbalize their marks
				for (ExamStudent student : students) {
				    if (student.getResultState().equals("PUBBLICATO") || student.getResultState().equals("RIFIUTATO") ||
				    		student.getResultState().equals("ASSENTE")){
				        checkVerbalize = true;
				        break;
				    }
				}
			}		
		} catch (SQLException e) {
			// throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in enrolled students database extraction");
			return;
		}
		String path = "/WEB-INF/EnrolledStudentsPage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("students", students);
		ctx.setVariable("courseId",chosenCourseId);
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
