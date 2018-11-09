package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
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
import javafx.util.Pair;

class SearchResponse{
	ArrayList<SearchFoodItem> food;
}

class CompareResponse{
	ArrayList<InfoResponse> comparation;
}

class InfoResponse{
	int foodId;
	String foodName;
	String calories;
	String protein;
	String vitamin;
	String sugar;
}

class SearchFoodItem{
	int foodId;
	String foodName;
}

class SaveContent{
	ArrayList<Integer> saving;
}

class SaveResponse{
	String type;
	String mealId;
}

class DeleteResponse{
	String type;
}

class ShareResponse{
	String type;
}

/**
 * Servlet implementation class SearchEngine
 */
@WebServlet("/SearchEngine")
public class SearchEngine extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DatabaseDriver databaseDriver = new DatabaseDriver();
	private Gson gson = new Gson();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchEngine() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String type = request.getParameter("type");
    	DatabaseDriver.connect();
    	PrintWriter out= response.getWriter();
    	HttpSession session = request.getSession();
    	// user searching for food
    	if(type != null && type.equals("search")) {
    		// get the search key
    		String q = request.getParameter("q");
    		String searchKey[] = q.split("\\s+");
    		// response classes
    		ArrayList<SearchFoodItem> food = new ArrayList<SearchFoodItem>();
    		ArrayList<Pair<Integer, String> >searchResult = databaseDriver.SearchFood(searchKey);
    		for(int i=0; i<searchResult.size(); i++) {
    			SearchFoodItem newFood = new SearchFoodItem();
    			newFood.foodId = searchResult.get(i).getKey();
    			newFood.foodName = searchResult.get(i).getValue();
    			food.add(newFood);
    		}
    		try {
				String toPass = gson.toJson(food);
				out.println(toPass);
			} catch (Exception e) {
				System.out.println("JSON: " + e.getMessage());
			}
    	}
    	
    	// user getting food information
    	if(type != null && type.equals("getFood")) {
    		// make the query
    		String foodID = request.getParameter("foodId");
    		Map<String, String> result = databaseDriver.getFoodInfo(foodID);
    		
    		// pass the information to the response object
    		InfoResponse ir = new InfoResponse();
    		System.out.println(result.get("calories"));
    		ir.foodId = Integer.parseInt(foodID);
    		ir.calories = result.get("calories");
    		ir.foodName = result.get("foodName");
    		ir.protein = result.get("protein");
    		ir.sugar = result.get("sugar");
    		try {
				String toPass = gson.toJson(ir);
				out.println(toPass);
				System.out.println(toPass);
			} catch (Exception e) {
				System.out.println("JSON: " + e.getMessage());
			}
    	}
    	
    	// user comparing two food
    	if(type != null && type.equals("compare")) {
    		String foodID1 = request.getParameter("food1");
    		String foodID2 = request.getParameter("food2");
    		InfoResponse ir1 = new InfoResponse();
    		InfoResponse ir2 = new InfoResponse();
    		// make the query for food1
    		Map<String, String> result1 = databaseDriver.getFoodInfo(foodID1);
    		// pass the information to the response object
    		ir1.foodId = Integer.parseInt(foodID1);
    		ir1.calories = result1.get("calories");
    		ir1.foodName = result1.get("foodName");
    		ir1.protein = result1.get("protein");
    		ir1.sugar = result1.get("sugar");
    		
    		// make query for food2
    		Map<String, String> result2 = databaseDriver.getFoodInfo(foodID2);
    		// pass the information to the response object
    		ir2.foodId = Integer.parseInt(foodID2);
    		ir2.calories = result2.get("calories");
    		ir2.foodName = result2.get("foodName");
    		ir2.protein = result2.get("protein");
    		ir2.sugar = result2.get("sugar");
    		System.out.println(ir2.calories);
    		// create the compare response
    		ArrayList<InfoResponse> lir = new ArrayList<InfoResponse>();
    		lir.add(ir1);
    		lir.add(ir2);
    		try {
				String toPass = gson.toJson(lir);
				out.println(toPass);
				System.out.println(toPass);
			} catch (Exception e) {
				System.out.println("JSON: " + e.getMessage());
			}
    	}
    	// user saving meal
    	if(type != null && type.equals("saveMeal")) {
    		String dietName = request.getParameter("name");
    		String content = request.getParameter("content");
    		//content = "[01001, 01002, 01003, 01004, 01005]";
    		System.out.println("name: " + dietName);
    		System.out.println("content: " + content);
    		String contentJson = "{ \"saving\": " + content + "}";
    		
    		SaveResponse sr = new SaveResponse();
    		
    		//session.setAttribute("userID", 1);

    		int userID = 0;
    		try {
    			userID = (int)session.getAttribute("userID");
    		} catch (NullPointerException npe) {
    			// if the session is not set, that means the user is a guest
    			// then catch the nullPointerException and return the error message to the front end
    			System.out.println("npe: " + npe.getMessage());
    			sr.type = "notuser";
    			try {
    				String toPass = gson.toJson(sr);
    				out.println(toPass);
    			} catch (Exception e) {
    				System.out.println("JSON: " + e.getMessage());
    			}
    			DatabaseDriver.close();
    			return;
    		}
    		
    		// else, the user is not a guest, continue with saving
    		System.out.println("userID: " + userID);
    		// get the array of food ID
    		SaveContent Content = new SaveContent();
    		try {
				Content = gson.fromJson(contentJson, SaveContent.class);
			} catch (Exception e) {
				System.out.println("That file is not a well-formed JSON file");
			}

    		// cast the content of the diet to an strings
    		ArrayList<String> food = new ArrayList<String>();
    		for(int i=0; i<Content.saving.size(); i++) {
    			food.add(Content.saving.get(i).toString());
    		}
    		int dietId = databaseDriver.InsertDiet(userID, food, dietName);
    		sr.mealId =	Integer.toString(dietId);
    		sr.type = "success";
    		try {
				String toPass = gson.toJson(sr);
				out.println(toPass);
			} catch (Exception e) {
				System.out.println("JSON: " + e.getMessage());
			}
    		
    	}
    	
    	// user deleting meal
    	//type = "deleteMeal";
    	if(type != null && type.equals("deleteMeal")) {
    		String mealID = request.getParameter("mealId");
    		int userID = (int)session.getAttribute("userID");
//    		mealID = "1";
//    		userID = 2;
    		DeleteResponse dr = new DeleteResponse();
    		if (databaseDriver.deleteMeal(userID, mealID)) {
    			dr.type = "success";
    		}
    		else {
    			dr.type = "Fail to delete the meal: the specified meal does not exist";
    		}
    		try {
				String toPass = gson.toJson(dr);
				out.println(toPass);
			} catch (Exception e) {
				System.out.println("JSON: " + e.getMessage());
			}
    	}
    	
    	//type = "shareMeal";
    	// user is sharing a specified meal with someone else --> make the meal public
    	if(type != null && type.equals("shareMeal")) {
    		String dietID = request.getParameter("mealId");
    		int userID = (int)session.getAttribute("userID");
//    		dietID = "3";
//    		userID = 2;
    		ShareResponse sr = new ShareResponse();
    		if(databaseDriver.tangleSharing(userID, dietID)) {
    			sr.type = "success";
    		}
    		else {
    			sr.type = "fail: the specified meal does not exist for the user!";
    		}
    		try {
				String toPass = gson.toJson(sr);
				out.println(toPass);
			} catch (Exception e) {
				System.out.println("JSON: " + e.getMessage());
			}
    	}
    	DatabaseDriver.close();
    }
}








