//CheckLogin
package controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import beans.User;
import dao.UserDAO;
import utility.DbConnection;
import javax.servlet.http.HttpSession;
import java.util.UUID;



@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CheckLogin() {
		super();
	}

	public void init() throws ServletException {
		connection = DbConnection.connect(getServletContext());

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		
		

		
		
		
		
				
		
		String usrn = request.getParameter("username");
		String pwd = request.getParameter("pwd");
		String role = request.getParameter("role");
		
		/*if (usrn == null || usrn.isEmpty() || pwd == null || pwd.isEmpty() || role == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}*/
		
		UserDAO usr = new UserDAO(connection);
		User u = null;
		try {
			if(role.equals("teacher"))
				u = usr.checkCredentialsTeacher(usrn, pwd);
			else if(role.equals("student"))
				u = usr.checkCredentialsStudent(usrn, pwd);

		} catch (SQLException e) {
			// throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential checking"+" "+role);
			return;
		}
		
		
		String path = getServletContext().getContextPath();
		if (u == null) {
			path = getServletContext().getContextPath() + "/index.html";
		} else {
			// Ottieni l'oggetto HttpSession
			HttpSession session = request.getSession();
			
			// Genera un nuovo identificatore per la sessione
			String sessionId = UUID.randomUUID().toString();
			
			request.getSession().setAttribute("user", u);
			
			System.out.println(sessionId);

			// Imposta l'attributo "sessionId" nella sessione
			session.setAttribute("sessionId", sessionId);
			
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
		}
	}
}
