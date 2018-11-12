package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

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

class LogoutResponse{
	String type;
}

class SuggestMealResponse{
	int mealId;
	String foodItems;
	String createdBy;
}

class FollowRelationResponse{
	String message;
}


/**
 * Servlet implementation class UserManager
 */
@WebServlet("/UserManager")
public class UserManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DatabaseDriver databaseDriver = new DatabaseDriver();
	private Gson gson = new Gson();
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
    	System.out.println("current type: " + type);
    	PrintWriter out = response.getWriter();
    	databaseDriver.connect(); // connect to the database
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
				Response r = this.getResponse(type, status);
				String toPass = gson.toJson(r);
				out.println(toPass);
				// store the current user in the session
				if(r.type.equals("success")) {
					session.setAttribute("userEmail", email);
					System.out.println("in userManager, userEmail: " + email);
					int userID = databaseDriver.getCurrUserID(email);
					session.setAttribute("userID", userID);
					System.out.println("userID: " + userID);
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
				Response r = this.getResponse(type, status);
				String toPass = gson.toJson(r);
				out.println(toPass);
				if(r.type.equals("success")) {
					session.setAttribute("userEmail", email);
					int userID = databaseDriver.getCurrUserID(email);
					session.setAttribute("userID", userID);
				}
			} catch (Exception e) {
				System.out.println("JSON: " + e.getMessage());
			}
    	}
    	
    	if(type != null && type.equals("logout")) {
    		LogoutResponse lr = new LogoutResponse();
    		lr.type = "success";
    		try {
    			String toPass = gson.toJson(lr);
    			out.println(toPass);
    		} catch (Exception e) {
    			System.out.println("JSON: " + e.getMessage());
    		}
    	}
    	
    	// making suggestion of users to the current user
    	//type = "Suggestions";
    	//type = "suggestedMeals";
    	if(type != null && type.equals("Suggestions")) {
    		System.out.println("in suggestions");
    		//int numSuggest = Integer.parseInt(request.getParameter("number"));
    		//int currUser = (int)session.getAttribute("userID");
    		
    		// ***for testing
    		int currUser = 1;
    		int numSuggest = 2;
    		// ***
    		
    		ArrayList<Map<String, String> > SuggestUser = databaseDriver.SuggestUser(numSuggest, currUser);
    		try {
    			String toPass = gson.toJson(SuggestUser);
    			out.println(toPass);
    		} catch(Exception e) {
    			System.out.println("JSON: " + e.getMessage());
    		}
    	}
    	
    	if(type != null && type.equals("suggestedMeals")) {
    		System.out.println("in suggested meals");
    		
    		// ***for DEBUG only
    		int numToSuggest = 2;
    		int currUser = 1;
    		// ***
    		
    		//System.out.println("num from front end: " + request.getParameter("number"));
    		//System.out.println("num: " + numToSuggest);
    		
    		//int currUser = (int)session.getAttribute("userID");
    		//int numToSuggest = Integer.parseInt(request.getParameter("number"));

    		ArrayList<Map<String, String>> result = databaseDriver.SuggestMeal(currUser, numToSuggest);

    		try {
    			String toPass = gson.toJson(result);
    			System.out.println("toPass: " + toPass);
    			out.println(toPass);
    		} catch (Exception e) {
    			System.out.println("JSON: " + e.getMessage());
    		}
    	}
    	
    	// return all followers of the current user
    	if(type != null && type.equals("followers")) {
    		System.out.println("in followers");
    		int currUser = (int) session.getAttribute("userID");
    		//int currUser = 2;
    		ArrayList<Map<String, String> > result = databaseDriver.getFollowers(currUser);
    		try {
    			String toPass = gson.toJson(result);
    			out.println(toPass);
    		} catch (Exception e) {
    			System.out.println("sqle: " + e.getMessage());
    		}
    	}
    	
    	if(type != null && type.equals("FollowRelation")) {
    		int target = Integer.parseInt(request.getParameter("userId"));
    		int currUser = (int) session.getAttribute("userID");
    		FollowRelationResponse frr = new FollowRelationResponse();
    		boolean tangle = databaseDriver.tangleFollowRelation(currUser, target);
    		// they are originally friend
    		if(tangle) {
    			frr.message = "relation deleted";
    		}
    		else {
    			frr.message = "relation added";
    		}
    		try {
    			String toPass = gson.toJson(frr);
    			out.println(toPass);
    		} catch (Exception e) {
    			System.out.println("JSON: " + e.getMessage());
    		}
    	}
    	databaseDriver.close(); // close the connection
    }
}





















