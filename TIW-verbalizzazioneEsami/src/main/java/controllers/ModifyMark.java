package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.ExamStudent;
import beans.User;
import utility.DbConnection;
import dao.CourseDAO;
import dao.ExamDAO;

@WebServlet("/ModifyMark")
public class ModifyMark extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	List<String> marks = new ArrayList<>(Arrays.asList("ASSENTE", "RIPROVATO", "RIMANDATO", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "30L"));

	public ModifyMark() {
		super();
	}

	public void init() throws ServletException {
		connection = DbConnection.connect(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession s = request.getSession();
		String homePage = request.getServletContext().getContextPath() + "/GoToHomeTeacher";
		
		User user = (User) s.getAttribute("user");
		
		String chosenCourse = request.getParameter("courseId");
		int chosenCourseId = Integer.parseInt(chosenCourse);
		String chosenExam = request.getParameter("examDate");
		String matricolaSelected = request.getParameter("matricola");
		String examMark = request.getParameter("examMark");
		
		ExamDAO eDao = new ExamDAO(connection, chosenCourseId, chosenExam);

		//check permissions
		try { 
			//checking if the selected course exists
			CourseDAO cDao = new CourseDAO(connection, chosenCourseId);
			if(cDao.findCourse() == null) {
				response.sendRedirect(homePage);
				return;
			}
			//checking if the current teacher owns the selected course
			String currTeacher = cDao.findOwnerTeacher();
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
		
		//checking if the mark inserted is valid
		if(marks.contains(examMark)){
			ExamStudent examStud = new ExamStudent();
			try {
				examStud = eDao.getResult(matricolaSelected);
				//checking if the mark is already published or verbalized
				if(!(examStud.getResultState()).equals("PUBBLICATO")&& !(examStud.getResultState()).equals("VERBALIZZATO")) {
					//change the mark of the student
					eDao.changeMark(matricolaSelected, examMark);
				}
			}catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in student's exams database extraction");
			}
		
		String path = "/GoToEnrolledStudents";
		request.setAttribute("examDate", chosenExam);
		request.setAttribute("courseId", chosenCourse);
		request.getRequestDispatcher(path).forward(request, response);

		}else {
			String path = "/GoToModifyPage";
			request.setAttribute("examDate", chosenExam);
			request.setAttribute("courseId", chosenCourse);
			request.setAttribute("matricola", matricolaSelected);
			request.getRequestDispatcher(path).forward(request, response);
		}
	}
}


