package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import utility.DbConnection;
import dao.ExamDAO;

@WebServlet("/ModifyMark")
public class ModifyMark extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	


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
		String chosenCourse = request.getParameter("courseId");
		int chosenCourseId = Integer.parseInt(chosenCourse);
		String chosenExam = request.getParameter("examDate");
		String matricolaSelected = request.getParameter("matricola");
		String examMark = request.getParameter("examMark");
		ExamDAO eDao = new ExamDAO(connection, chosenCourseId, chosenExam);
		
		if(examMark.equals("30L") || examMark.equals("RIPROVATO") || examMark.equals("ASSENTE") || (Integer.parseInt(examMark)>=18 && Integer.parseInt(examMark)<=30)){
			try {
			eDao.changeMark(matricolaSelected, examMark);
			}catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failure in student's exams database updating");
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


