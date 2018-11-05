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

/**
 * Servlet implementation class SearchEngine
 */
@WebServlet("/SearchEngine")
public class SearchEngine extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DatabaseDriver databaseDriver = new DatabaseDriver();
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
    		SearchResponse sr = new SearchResponse();
    		sr.food = food;
    		try {
				Gson gson = new Gson();
				String toPass = gson.toJson(sr);
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
    		ir.foodId = Integer.parseInt(foodID);
    		ir.calories = result.get("calories");
    		ir.foodName = result.get("foodName");
    		ir.protein = result.get("protein");
    		ir.sugar = result.get("sugar");
    		try {
				Gson gson = new Gson();
				String toPass = gson.toJson(ir);
				out.println(toPass);
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
    		CompareResponse cr = new CompareResponse();
    		// make the query for food1
    		Map<String, String> result1 = databaseDriver.getFoodInfo(foodID1);
    		Map<String, String> result2 = databaseDriver.getFoodInfo(foodID2);
    		// pass the information to the response object
    		ir1.foodId = Integer.parseInt(foodID1);
    		ir1.calories = result1.get("calories");
    		ir1.foodName = result1.get("foodName");
    		ir1.protein = result1.get("protein");
    		ir1.sugar = result1.get("sugar");
    		
    		ir2.foodId = Integer.parseInt(foodID2);
    		ir2.calories = result2.get("calories");
    		ir2.foodName = result2.get("foodName");
    		ir2.protein = result2.get("protein");
    		ir2.sugar = result2.get("sugar");
    		
    		ArrayList<InfoResponse> lir = new ArrayList<InfoResponse>();
    		lir.add(ir1);
    		lir.add(ir2);
    		cr.comparation = lir;
    		try {
				Gson gson = new Gson();
				String toPass = gson.toJson(cr);
				out.println(toPass);
			} catch (Exception e) {
				System.out.println("JSON: " + e.getMessage());
			}
    	}
    	
    	if(type != null && type.equals("saveMeal")) {
    		String dietName = request.getParameter("name");
    		String content = request.getParameter("content");
    		String contentJson = "{ \"saving\": " + content + "}";
    		// get the array of food ID
    		SaveContent Content = new SaveContent();
    		try {
				Gson gson = new Gson();
				Content = gson.fromJson(contentJson, SaveContent.class);
			} catch (Exception e) {
				System.out.println("That file is not a well-formed JSON file");
			}
    		
    	}
    	DatabaseDriver.close();
    }
}
