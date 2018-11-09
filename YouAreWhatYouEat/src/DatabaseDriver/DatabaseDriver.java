package DatabaseDriver;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import Servlets.SearchEngine;
import javafx.util.Pair;
public class DatabaseDriver {
	private static Connection conn = null;
	private static ResultSet rs = null;
	private static PreparedStatement ps = null;
	private static Statement s = null;
	
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
	public static void connect(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutrition?user=root&password=peter_sheng&useSSL=false");
			System.out.println("Database connected!");
		} catch (ClassNotFoundException e) {
			System.out.println("cnf: " + e.getMessage());
		} catch (SQLException e) {
			System.out.println("sqle: " + e.getMessage());
		}
	}
	
	// method to close the connection
	public static void close(){
		try{
			if (rs!=null){
				rs.close();
				rs = null;
			}
			if(conn != null){
				conn.close();
				conn = null;
			}
			if(ps != null ){
				ps = null;
			}
		}catch(SQLException sqle){
			System.out.println("connection close error" + sqle.getMessage());
		}
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
		} catch (SQLException sqle){
			System.out.println("sqle: " + sqle.getMessage());
		}
		return rs;
	}
	
	// method to search for food according to the key given
	public ArrayList<Pair<Integer, String> > SearchFood(String []searchKey) {
		ArrayList<Pair<Integer, String> > result = new ArrayList<Pair<Integer, String> >();
		// search each key one by one
		for(int i=0; i<searchKey.length; i++) {
			try {
				rs = this.QueryNutrition(searchKey[i]);
				int count = 0;
				while(rs.next() && count < 20) {
					String ID = rs.getString("FOOD_DES.NDB_No");
					System.out.println(ID);
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
				System.out.println("to insert food: " + food.get(i));
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
			System.out.println("num: " + numDietInDietUser);
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
				ps.setString(3, picture);
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
			while(rs.next()) {
				userID = rs.getInt("userID");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userID;
	}
	// End of User database query code
	//***********************************************************************************************************************************************
	
}











