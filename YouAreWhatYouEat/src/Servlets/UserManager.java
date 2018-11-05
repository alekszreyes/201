package Servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import DatabaseDriver.DatabaseDriver;


class Response{
	String type;
	String message;
}


/**
 * Servlet implementation class UserManager
 */
@WebServlet("/UserManager")
public class UserManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DatabaseDriver databaseDriver = new DatabaseDriver();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserManager() {
        super();
        // TODO Auto-generated constructor stub
    }
    // helper method to generate a response
    private Response getResponse(String status, String type) {
    	Response r = new Response();
    	r.type = type;
    	if(status.equals("login")) {
    		if(type.equals("success")) {
    			r.message = "Successfully logged in!";
    		}
    		else if(type.equals("notexist")) {
    			r.message = "The specified the user does not exist! Please check your email and password again!";
    		}
    		else if(type.equals("invalid")) {
    			r.message = "Error! Some information are missing!";
    		}
    	}
    	else if(status.equals("register")) {
    		if(type.equals("success")) {
    			r.message = "Successfully registered!";
    		}
    		else if(type.equals("exists")) {
    			r.message = "Account already exists with this email!";
    		}
    		else if(type.equals("invalid")) {
    			r.message = "Error! Some information are missing!";
    		}
    	}
    	return r;
    }
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String type = request.getParameter("type");
    	PrintWriter out = response.getWriter();
    	DatabaseDriver.connect(); // connect to the database
    	HttpSession session = request.getSession();
    	// user registering
    	if(type != null && type.equals("register")) {
    		String status; // string to store the response status
    		// reading in variables
    		String firstName = request.getParameter("firstName");
    		String lastName = request.getParameter("lastName");
    		String email = request.getParameter("email");
    		String picture = request.getParameter("picture");
    		String password = request.getParameter("password");
    		System.out.println("firstname: " + firstName);
    		System.out.println("lastname: " + lastName);
    		System.out.println("email: " + email);
    		//System.out.println("picture: " + picture);
    		System.out.println("password: " + password);
    		
    		if(firstName == null || lastName == null || email == null || picture == null || password == null
    		|| firstName.equals("") || lastName.equals("") || email.equals("") || picture.equals("") ) {
    			status = "invalid";
    		}
    		else {
    			status = databaseDriver.QueryUserRegister(email, password, firstName, lastName, picture); 
    		}
    		
    		// create the JSON object to and pass it to the front end
    		try {
				Gson gson = new Gson();
				Response r = this.getResponse(type, status);
				String toPass = gson.toJson(r);
				out.println(toPass);
				// store the current user in the session
				if(r.type.equals("success")) {
					session.setAttribute("currUserEmail", email);
				}
			} catch (Exception e) {
				System.out.println("JSON: " + e.getMessage());
			}
    	}
    	
    	// user logging in
    	if(type != null && type.equals("login")) {
    		String status;
    		// reading in variables
    		String email = request.getParameter("email");
    		String password = request.getParameter("password");
    		if(email == null || password == null || email.equals("") || password.equals("")) {
    			status = "invalid";
    		}
    		else {
    			status = databaseDriver.QueryUserLogin(email, password);
    		}
    		System.out.println("status: " + status);
    		// create the JSON object to and pass it to the front end
    		try {
    			System.out.println(status);
				Gson gson = new Gson();
				Response r = this.getResponse(type, status);
				String toPass = gson.toJson(r);
				out.println(toPass);
				if(r.type.equals("success")) {
					session.setAttribute("currUserEmail", email);
				}
			} catch (Exception e) {
				System.out.println("JSON: " + e.getMessage());
			}
    	}
    	DatabaseDriver.close(); // close the connection
    }
}





















