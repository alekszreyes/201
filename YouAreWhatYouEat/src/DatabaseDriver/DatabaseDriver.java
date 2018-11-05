package DatabaseDriver;

import java.sql.*;
import java.util.ArrayList;
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
	// Begin of JDBC code
	// method to connect to the database
	public static void connect(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutrition?user=root&password=lunjohnzhang&useSSL=false");
			//connFood = DriverManager.getConnection("jdbc:mysql://localhost/YouAreWhatYouEatUsers?user=root&password=lunjohnzhang&useSSL=false");
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
					"WHERE FOOD_DES.Long_Desc LIKE ?\n" + 
					"AND FOOD_DES.NDB_No = NUT_DATA.NDB_No \n" + 
					"AND NUT_DATA.Nutr_No = NUTR_DEF.Nutr_No";
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, toQuery);
			rs = ps.executeQuery();
			// process the data below
			
			
		} catch (SQLException sqle){
			System.out.println("sqle: " + sqle.getMessage());
		}
		return rs;
		
	}
	
	public ResultSet QueryNutritionID(String toQuery){
		String query =  "SELECT FOOD_DES.NDB_No, FOOD_DES.Long_Desc, NUT_DATA.NDB_No, NUT_DATA.Nutr_No, NUT_DATA.Nutr_Val, NUTR_DEF.NutrDesc, NUTR_DEF.Units\n" + 
					"FROM FOOD_DES, NUT_DATA, NUTR_DEF\n" + 
					"WHERE FOOD_DES.Long_Desc LIKE ?\n" + 
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
				while(rs.next()) {
					String ID = rs.getString("FOOD_DES.NDB_No");
					System.out.println(ID);
					String foodName = rs.getString("FOOD_DES.Long_Desc");
					Pair<Integer, String> toAdd = new Pair<Integer, String>(Integer.parseInt(ID), foodName);
					result.add(toAdd);
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
	
	// Begin of nutrition database update code
	//***********************************************************************************************************************************************
	public void InsertDiet(String userEmail, ArrayList<String> food, String dietName) {
		
	}
	
	
	// End of nutrition database update code
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
			System.out.println(query);
			System.out.println(ps);
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
	// End of User database query code
	//***********************************************************************************************************************************************
	
}











