package controllers;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.User;
import dao.UserDAO;
import utility.DbConnection;


@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CheckLogin() {
		super();
	}

	public void init() throws ServletException {
		//connection with DB
		connection = DbConnection.connect(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//get of parameters insert by the user
		String usrn = request.getParameter("username");
		String pwd = request.getParameter("pwd");
		String role = request.getParameter("role");
		
		UserDAO usr = new UserDAO(connection);
		User u = null;
		String path = getServletContext().getContextPath();
		
		try {
			if(role.equals("teacher"))
				//check of teacher credentials
				u = usr.checkCredentialsTeacher(usrn, pwd);
			else if(role.equals("student"))
				//check of student credentials
				u = usr.checkCredentialsStudent(usrn, pwd);
		} catch (SQLException e) {
			// throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential checking"+" "+role);
			return;
		}
		
		
		
		if (u == null) {
		    path = getServletContext().getContextPath() + "/index.html?";
		} else {
			HttpSession session = request.getSession();
			request.getSession().setAttribute("user", u);
			session.setAttribute("user", u);
			//redirect to the appropriate servlet depending on the role of the user
			String target = (u.getRole().equals("teacher")) ? "/GoToHomeTeacher" : "/GoToHomeStudent";
			path = path + target;
		}
		response.sendRedirect(path);
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
