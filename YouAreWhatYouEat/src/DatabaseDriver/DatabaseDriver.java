package DatabaseDriver;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import Servlets.SearchEngine;
import javafx.util.Pair;
public class DatabaseDriver {
	private Connection conn = null;
	private ResultSet rs = null;
	private PreparedStatement ps = null;
	private Statement s = null;
	
	//***********************************************************************************************************************************************
	// Begin of universal helper functions
	// method to pre-process the foodID passed in the make sure that its length is 5,
	// which is the length of the foodID inside the database
	String StandardizeDietID(String dietID) {
		if(dietID.length() != 5) {
			for(int i=0; i<5-dietID.length(); i++) {
				dietID = "0" + dietID;
			}
		}
		String standardized = dietID;
		return standardized;
	}
	
	// End of universal helper functions
	
	//***********************************************************************************************************************************************
	// Begin of JDBC code
	// method to connect to the database
	public void connect(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutrition?user=root&password=lunjohnzhang&useSSL=false");
			System.out.println("Database connected!");
		} catch (ClassNotFoundException e) {
			System.out.println("cnf: " + e.getMessage());
		} catch (SQLException e) {
			System.out.println("sqle: " + e.getMessage());
		}
	}
	
	// method to close the connection
	public void close(){
//		try{
//			if (rs!=null){
//				rs.close();
//				rs = null;
//			}
//			if(conn != null){
//				conn.close();
//				conn = null;
//			}
//			if(ps != null ){
//				ps = null;
//			}
//		}catch(SQLException sqle){
//			System.out.println("connection close error" + sqle.getMessage());
//		}
		System.out.println("Connection closed!");
	}
	
	// End of JDBC code
	//***********************************************************************************************************************************************
	
	//***********************************************************************************************************************************************
	// Begin of nutrition database query code
	
	// method to query nutrition of a specific food
	public ResultSet QueryNutrition(String toQuery){
		String query =  "SELECT FOOD_DES.NDB_No, FOOD_DES.Long_Desc, NUT_DATA.NDB_No, NUT_DATA.Nutr_No, NUT_DATA.Nutr_Val, NUTR_DEF.NutrDesc, NUTR_DEF.Units\n" + 
					"FROM FOOD_DES, NUT_DATA, NUTR_DEF\n" + 
					"WHERE FOOD_DES.Long_Desc LIKE ? \n" + 
					"AND FOOD_DES.NDB_No = NUT_DATA.NDB_No \n" + 
					"AND NUT_DATA.Nutr_No = NUTR_DEF.Nutr_No";
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, "%" + toQuery+ "%");
			rs = ps.executeQuery();			
			
		} catch (SQLException sqle){
			System.out.println("sqle: " + sqle.getMessage());
		}
		return rs;
		
	}
	
	public ResultSet QueryNutritionID(String toQuery){
		toQuery = this.StandardizeDietID(toQuery);
		String query = "SELECT FOOD_DES.NDB_No, FOOD_DES.Long_Desc, NUT_DATA.NDB_No, NUT_DATA.Nutr_No, NUT_DATA.Nutr_Val, NUTR_DEF.NutrDesc, NUTR_DEF.Units\n" + 
					"FROM FOOD_DES, NUT_DATA, NUTR_DEF\n" + 
					"WHERE FOOD_DES.NDB_No = ?\n" + 
					"AND FOOD_DES.NDB_No = NUT_DATA.NDB_No \n" + 
					"AND NUT_DATA.Nutr_No = NUTR_DEF.Nutr_No";
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, toQuery);
			rs = ps.executeQuery();
//			rs.next();
//			System.out.println("in querynutrition: " + rs.getString("FOOD_DES.Long_Desc"));
		} catch (SQLException sqle){
			System.out.println("sqle: " + sqle.getMessage());
		}
		
		return rs;
	}
	
	public String getFoodName(String foodID) {
		
		rs = QueryNutritionID(foodID);
		String result = "";
		try {
			//rs.next();
			//System.out.println("in querynutrition: " + rs.getString("FOOD_DES.Long_Desc"));
			while(true) {
				if(!rs.next()) break;
				if (rs.isAfterLast()) break;
				result = rs.getString("FOOD_DES.Long_Desc");
			}
		} catch (SQLException e) {
			System.out.println("sqle: " + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	// method to search for food according to the key given
	public ArrayList<Pair<Integer, String> > SearchFood(String []searchKey) {
		ArrayList<Pair<Integer, String> > result = new ArrayList<Pair<Integer, String> >();
		// search each key one by one
		for(int i=0; i<searchKey.length; i++) {
			try {
				ResultSet rs = this.QueryNutrition(searchKey[i]);
				int count = 0;
				while(rs.next() && count < 20) {
					String ID = rs.getString("FOOD_DES.NDB_No");
					//System.out.println(ID);
					String foodName = rs.getString("FOOD_DES.Long_Desc");
					Pair<Integer, String> toAdd = new Pair<Integer, String>(Integer.parseInt(ID), foodName);
					if(!result.contains(toAdd)) {
						result.add(toAdd);
						count++;
					}
				}
			} catch(SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return result;
	}
	
	// method to get the food info from according to the id of the food given
	public Map<String, String> getFoodInfo(String foodID){
		Map<String, String> result = new HashMap();
		rs = QueryNutritionID(foodID);
		try {
			String calories = null;
			String protein = null;
			String vitamin= null;
			String sugar = null;
			String foodName = null;
			while(rs.next()) {
				foodName = rs.getString("FOOD_DES.Long_Desc");
				if(rs.getString("NutrDesc").toLowerCase().contains("energy") && rs.getString("Units").toLowerCase().equals("kcal")) {
					calories = rs.getString("Nutr_Val") + " " + rs.getString("Units");
				}
				else if(rs.getString("NutrDesc").toLowerCase().contains("protein")) {
					protein = rs.getString("Nutr_Val") + " " + rs.getString("Units");
				}
				else if(rs.getString("NutrDesc").toLowerCase().contains("vitamin c")) {
					vitamin = rs.getString("Nutr_Val") + " " + rs.getString("Units");
				}
				else if(rs.getString("NutrDesc").toLowerCase().contains("sugar")) {
					sugar = rs.getString("Nutr_Val") + " " + rs.getString("Units");
				}
			}
			// after getting all of the nutrition facts, create pairs and insert them into the result
			result.put("calories", calories);
			result.put("protein", protein);
			result.put("vitamin", vitamin);
			result.put("sugar", sugar);
			result.put("foodName", foodName);
		} catch (SQLException sqle) {
			System.out.println("sqle: " + sqle.getMessage());
		}
		
		return result;
	}
	
	// method to turn all of the meals of a specific user
	public ArrayList<Map<String, String>> getMeals(int currUser){
		ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
		String query = "SELECT * FROM DietUser \n"
				+ "WHERE DietUser.userID = ?\n";
		try {
			// get all dietID of the current user 
			ps = conn.prepareStatement(query);
			ps.setInt(1, currUser);
			rs = ps.executeQuery();
			ArrayList<Pair<Integer, Integer>> allDiet = new ArrayList<Pair<Integer, Integer>>();
			while(rs.next()) {
				Pair<Integer, Integer> toAdd = new Pair<Integer, Integer>(rs.getInt("dietID"), rs.getInt("access"));
				allDiet.add(toAdd);
			}
			//rs.close();
			// for each diet, get all of the relevant information
			query = "SELECT * FROM DietFood \n"
					+ "WHERE dietID = ?";
			PreparedStatement ps = conn.prepareStatement(query);
			for(int i=0; i<allDiet.size(); i++) {
				//System.out.println("currentDietID: " + allDiet.get(i).getKey());
				ps.setInt(1, allDiet.get(i).getKey());
				ResultSet rs = ps.executeQuery();
				
				Map<String, String> newMeal = new HashMap();
				
				// set access of the new meal
				if(allDiet.get(i).getValue() == 1) {
					newMeal.put("privacy", "Public");
				}
				else {
					newMeal.put("privacy", "Private");
				}
				
				// use an ArrayList to remember all of the food of the current meal
				ArrayList<String> allFood = new ArrayList<String>();
				while(rs.next()) {
					newMeal.put("mealId", Integer.toString(rs.getInt("dietID")));
					newMeal.put("name", rs.getString("dietName"));
					//System.out.println("name: " + rs.getString("dietName")); 
					String currFoodID = rs.getString("foodID");
					allFood.add(this.getFoodName(currFoodID));
				}
				String allFoodMsg = "";
				for(int j=0; j<allFood.size(); j++) {
					allFoodMsg += allFood.get(j);
					if(j != allFood.size() - 1) {
						allFoodMsg += ", ";
					}
				}
				newMeal.put("foodItems", allFoodMsg);
				result.add(newMeal);
				rs.close();
			}
		} catch (SQLException e) {
			System.out.println("sqle in getMeals: " + e.getMessage());
		}
		return result;
	}
	
	// End of nutrition database query code
	//***********************************************************************************************************************************************
	
	//***********************************************************************************************************************************************
	// Begin of diet database update code
	public int InsertDiet(int userID, ArrayList<String> food, String dietName) {
		// standardize the food id in food
		for(int i=0; i<food.size(); i++) {
			food.set(i, this.StandardizeDietID(food.get(i)));
		}
		//get the userID and the current largest dietID
		String query = "";
		int dietID = 0;
		try {
			// find the current largest Diet ID
			query = "SELECT dietID FROM DietFood ORDER BY dietID DESC LIMIT 1";
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			while(rs.next()) {
				dietID = rs.getInt("dietID");
			}
			dietID += 1; // get the new id by incrementing the largest id
			
			for(int i=0; i<food.size(); i++) {
				// insert into dietFood
				query = "INSERT INTO DietFood(dietID, dietName, foodID) VALUES(?, ?, ?)";
				ps = conn.prepareStatement(query);
				ps.setInt(1, dietID);
				ps.setString(2, dietName);
				//System.out.println("to insert food: " + food.get(i));
				ps.setString(3, food.get(i));
				ps.executeUpdate();
			}
			// insert into dietUser
			query = "INSERT INTO DietUser(userID, dietID, creationTime, access) VALUES(?, ?, ?, ?)";
			ps = conn.prepareStatement(query);
			ps.setInt(1, userID);
			ps.setInt(2, dietID);
			// get SQL style current date
			java.sql.Date sqlDate = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
			ps.setString(3, sqlDate.toString());
			ps.setInt(4, 0);
			ps.executeUpdate();
		} catch (SQLException sqle) {
			System.out.println("sqle: " + sqle.getMessage());
		}
		return dietID;
	}
	
	public boolean deleteMeal(int userID, String mealID) {
		int dietID = Integer.parseInt(mealID);
		try {
			String query = "SELECT dietID FROM DietUser WHERE dietID = ?";
			// get the number of appearance of the specified diet in DietUser table and DietFood table
			// number of appearance in DietUser
			ps = conn.prepareStatement(query);
			ps.setInt(1, dietID);
			rs = ps.executeQuery();
			int numDietInDietUser = 0;
			while(rs.next()) {
				numDietInDietUser++;
			}
			//System.out.println("num: " + numDietInDietUser);
			if(numDietInDietUser == 0) {
				return false;
			}
			// if only one user has the current diet, delete the diet from both tables
			// else delete only from DietUser
			query = "DELETE FROM DietUser WHERE dietID = ? AND userID = ?";
			ps = conn.prepareStatement(query);
			ps.setInt(1, dietID);
			ps.setInt(2, userID);
			ps.executeUpdate();
			if(numDietInDietUser == 1) {
				query = "DELETE FROM DietFood WHERE dietID = ?";
				ps = conn.prepareStatement(query);
				ps.setInt(1, dietID);
				ps.executeUpdate();
			}
		} catch (SQLException sqle) {
			System.out.println("sqle: " + sqle.getMessage());
		}
		return true;
	}
	
	public boolean tangleSharing(int userID, String mealID) {
		int dietID = Integer.parseInt(mealID);
		try {
			String query = "SELECT access FROM DietUser WHERE dietID = ? AND userID = ?";
			ps = conn.prepareStatement(query);
			ps.setInt(1, dietID);
			ps.setInt(2, userID);
			rs = ps.executeQuery();
			int currAccess = -1;
			while(rs.next()) {
				currAccess = rs.getInt("access");
			}
			// if the specified user with the meal is not found, return an error
			if(currAccess == -1) {
				return false;
			}
			// else, process the update
			query = "UPDATE DietUser SET access = ? WHERE dietID = ? AND userID = ?";
			ps = conn.prepareStatement(query);
			ps.setInt(2, dietID);
			ps.setInt(3, userID);
			if(currAccess == 0) {
				ps.setInt(1, 1);
			}
			else {
				ps.setInt(1, 0);
			}
			ps.executeUpdate();
		} catch (SQLException sqle) {
			System.out.println("sqle: " + sqle.getMessage());
		}
		return true;
	}
	
	// End of diet database update code
	//***********************************************************************************************************************************************
	
	
	//***********************************************************************************************************************************************
	// Begin of User database query code
	
	// method to validate when a user is trying to login: 
	// 1) exist or not
	// 2) if exist, information valid or not
	public String QueryUserLogin(String email, String password) {
		String msg = "";
		String query = "SELECT userEmail FROM Users WHERE userEmail=?";
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, email);
			rs = ps.executeQuery();
			String user = "";
			// process the data below
			while(rs.next()) {
				user = rs.getString("userEmail");
			}
			
			// user does not exist
			if(user.equals("")) {
				msg = "notexist";
			}
			// else, check if the user has the correct password
			else {
				query = "SELECT userEmail FROM Users WHERE userPassword=?";
				ps = conn.prepareStatement(query);
				ps.setString(1, password);
				rs = ps.executeQuery();
				user = "";
				while(rs.next()) {
					user = rs.getString("userEmail");
				}
				// password is not right, not exists
				if(user.equals("")) {
					msg = "notexists";
				}
				// else, password is correct, log in success
				else {
					msg = "success";
				}
			}
		} catch (SQLException sqle){
			System.out.println("sqle: " + sqle.getMessage());
		}
		
		return msg;
		 
	}
	
	// method to validate when a user is trying to register
	public String QueryUserRegister(String email, String password, String firstName, String lastName, String picture) {
		String msg = "";
		String query = "SELECT userEmail FROM Users WHERE userEmail=?";
		try {
			// check if the user has already registered
			ps = conn.prepareStatement(query);
			ps.setString(1, email);
			rs = ps.executeQuery();
			String user = "";
			while(rs.next()) {
				user = rs.getString("userEmail");
			}
			// no user found, the user can register
			if (user.equals("")) {
				//System.out.println(picture);
				// add the information of the user to the database
				query = "INSERT INTO Users(userName, userPassword, profilePic, userEmail) VALUES(?, ?, ?, ?)";
				ps = conn.prepareStatement(query);
				ps.setString(1, firstName + " " + lastName);
				ps.setString(2, password);
				ps.setString(3, email + ".png");
				ps.setString(4, email);
				ps.executeUpdate();
				msg = "success";
			}
			// or the user exists, the user do not need to register
			else {
				msg = "exists";
			}
		} catch (SQLException sqle){
			System.out.println("sqle: " + sqle.getMessage());
		}
		return msg;
		 
	}
	
	public int getCurrUserID(String userEmail) {
		int userID = -1;
		String query = "SELECT userID FROM Users WHERE userEmail = ?";
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, userEmail);
			rs = ps.executeQuery();
			while(rs.next()) {
				userID = rs.getInt("userID");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userID;
	}
	
	// method to get userName given id of the user
	public String getUserName(int userID) {
		String userName = "";
		String query = "SELECT * FROM Users WHERE userID = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setInt(1, userID);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				userName = rs.getString("userName");
			}
		} catch (SQLException e) {		
			System.out.println("sqle in getUserName: " + e.getMessage());
		}
		return userName;
	}
	
	public ArrayList<Map<String, String> > SuggestUser(int num, int currUser){
//		System.out.println("user currUser: " + currUser);
//		System.out.println("user num: " + num);
		
		
		ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
		
		// get all of the users that are not the current user
		String query = "SELECT * FROM Users, DietFood, DietUser \n" + 
				"WHERE DietUser.userID = Users.userID \n" + 
				"AND DietFood.dietID = DietUser.dietID\n" +
				"AND Users.userID != ?\n" +
				"AND Users.userID = ?";
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, currUser); // exclude the current user
			// loop through the number of users to return
			for(int i=1; i<=num; i++) {
				if(i == currUser) {
					num++;
					continue;
				}
				//System.out.println("i: " + i);
				ps.setInt(2, i);
				rs = ps.executeQuery();
				ArrayList<String> currMeal = new ArrayList<String>();
				int currMealID = 0;
				ArrayList<String> currFood = new ArrayList<String>();
				String currFoodID = "";
				Map<String, String> addUser = new HashMap();
				while(rs.next()) {
					addUser.put("userId", Integer.toString(rs.getInt("userID")));
					addUser.put("picture", rs.getString("profilePic"));
					addUser.put("name", rs.getString("userName"));
					
					// for meal and food, need to make sure they do not overlap
					// NOTE: diet and meal are equivalent
					if(currMealID != rs.getInt("dietID") && rs.getInt("access") == 1 && currMeal.size() != 2) {
						currMealID = rs.getInt("dietID");
						currMeal.add(rs.getString("dietName"));
					}
					
					if(!currFoodID.equals(rs.getString("foodID")) && rs.getInt("access") == 1 && currFood.size() != 2) {
						currFoodID = rs.getString("foodID");
						currFood.add(this.getFoodName(currFoodID));
					}
				}
				String mealMsg = "";
				String foodMsg = "";
				for(int j=0; j<currMeal.size(); j++) {
					mealMsg += currMeal.get(j);
					if(j != currMeal.size() - 1) {
						mealMsg += "; ";
					}
				}
				for(int j=0; j<currFood.size(); j++) {
					foodMsg += currFood.get(j);
					if(j != currMeal.size() - 1) {
						foodMsg += "; ";
					}
				}
				if(!mealMsg.isEmpty() && !foodMsg.isEmpty()) {
					addUser.put("meals", mealMsg);
					addUser.put("likes", foodMsg);
				}
				// *** DEBUG
				System.out.println("userID: " + addUser.get("userID"));
				System.out.println("picture: " + addUser.get("picture"));
				System.out.println("name: " + addUser.get("name"));
				System.out.println("meals: " + addUser.get("meals"));
				System.out.println("likes: " + addUser.get("likes") + "\n");
				// ***
				
				if(!addUser.isEmpty()) {
					result.add(addUser);
				}
				
			}
		} catch (SQLException e) {
			System.out.println("sqle User Suggestion: " + e.getMessage());
		}
		return result;
	}
	
	
	// method to return suggested meal
	public ArrayList<Map<String, String>> SuggestMeal(int currUser, int num){
		System.out.println("meal currUser: " + currUser);
		System.out.println("meal num: " + num);
		
		
		ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
		String query = "SELECT * FROM Users, DietFood, DietUser \n" + 
				"WHERE DietUser.userID = Users.userID \n" + 
				"AND DietFood.dietID = DietUser.dietID\n" +
				"AND Users.userID != ?\n";
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, currUser);
			ResultSet rs = ps.executeQuery();
			int currDiet = 0;
			// an ArrayList to store the food item list
			ArrayList<String> foodItemList = new ArrayList<String>();
			int suggested = 0;
			
			String currDietName = "";
			int currDietID = 0;
			String currCreator = "";
			String currFoodID = "";
			while(rs.next()) {
				Map<String, String> newMealToSuggest = new HashMap<String, String>();
				//if(rs.isAfterLast()) break;
				int dietIdInDS = rs.getInt("dietID");
				if(currDiet != dietIdInDS) {
					if(currDiet != 0) {
						newMealToSuggest.put("mealName", currDietName);
						newMealToSuggest.put("mealId", Integer.toString(currDietID));
						newMealToSuggest.put("createdBy", currCreator);
						String foodItem = "";
						for(int i=0; i<foodItemList.size(); i++) {
							foodItem += foodItemList.get(i);
							if(i != foodItemList.size()-1) {
								foodItem += "; ";
							}
						}
						newMealToSuggest.put("foodItems", foodItem);
						foodItemList.clear();
						result.add(newMealToSuggest);
						//newMealToSuggest.clear();
						suggested++;
						System.out.println("suggested: " + suggested);
						if(suggested == num) break;
					}
					currDiet = rs.getInt("dietID");
					currDietName = rs.getString("dietName");
					currDietID = rs.getInt("dietID");
					currCreator = rs.getString("userName");
					currFoodID = rs.getString("foodID");
					foodItemList.add(this.getFoodName(currFoodID));
					
				}
				else {
					foodItemList.add(this.getFoodName(rs.getString("foodID")));
				}
				System.out.println("currDiet after loop: " + currDiet);
				System.out.println("id in ds after loop: " + dietIdInDS);
			}
		} catch (SQLException sqle) {
			System.out.println("sqle in SuggestMeal: " + sqle.getMessage());
		}
		return result;
	}
	
	public ArrayList<Map<String, String> > getFollowers(int currUser){
		ArrayList<Map<String, String> > result = new ArrayList<Map<String, String> >();
		try {
			// get all of the users that the current user is following
			String query = "select * from FollowRelation where target = ?";
			ps = conn.prepareStatement(query);
			ps.setInt(1, currUser);
			rs = ps.executeQuery();
			ArrayList<Integer> followers = new ArrayList<Integer>();
			while(rs.next()) {
				followers.add(rs.getInt("from_"));
			}
			
			// get the information of the followers
			query = "SELECT * FROM Users WHERE userID = ?";
			ps = conn.prepareStatement(query);
			for(int i=0; i<followers.size(); i++) {
				Map<String, String> newFollower = new HashMap();
				ps.setInt(1, followers.get(i));
				ResultSet rs = ps.executeQuery();
				while(rs.next()) {
					newFollower.put("userId", Integer.toString(rs.getInt("userID")));
					newFollower.put("name", rs.getString("userName"));
					newFollower.put("picture", rs.getString("profilePic"));
				}
				if(!newFollower.isEmpty()) {
					result.add(newFollower);
//					System.out.println("userID: " + newFollower.get("userId"));
//					System.out.println("name: " + newFollower.get("name"));
//					System.out.println("picture: " + newFollower.get("picture"));
				}
			}
		} catch (SQLException sqle) {
			System.out.println("sqle: " + sqle.getMessage());
		}		
		return result;
	}
	
	public boolean tangleFollowRelation(int currUser, int target) {
		boolean result = false; // false means that they are originally not friends, but will be.
		String query = "SELECT * FROM FollowRelation\n"
					+ "WHERE from_ = ?\n"
					+ "AND target = ?";
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, currUser);
			ps.setInt(2, target);
			rs = ps.executeQuery();
			while(rs.next()) {
				result = true;
			}
			
			// update the relation in terms of the information given
			// they are originally friends, delete the relation
			if(result) {
				query = "DELETE FROM FollowRelation WHERE from_ = ? AND target = ?";
				ps = conn.prepareStatement(query);
				ps.setInt(1, currUser);
				ps.setInt(2, target);
				ps.executeUpdate();
			}
			// they are originally not friends, add the relation
			else {
				query = "INSERT INTO FollowRelation(from_, target) VALUES(?, ?)";
				ps = conn.prepareStatement(query);
				ps.setInt(1, currUser);
				ps.setInt(2, target);
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			System.out.println("sqle in tangleFollowRelation: " + e.getMessage());
		}
		return result;
	}
	
	// End of User database query code
	//***********************************************************************************************************************************************
	
}











