package DatabaseDriver;

import java.sql.*;
import java.util.ArrayList;

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
	
	// End of JDBC database query code
	//***********************************************************************************************************************************************
	
	//***********************************************************************************************************************************************
	// Begin of nutrition database query code
	
	// method to query nutrition database
	public ArrayList<String> QueryNutrition(String toQuery){
		ArrayList<String> result = new ArrayList<String>();
		String query =  "SELECT FOOD_DES.NDB_No, FOOD_DES.Long_Desc, NUT_DATA.NDB_No, NUT_DATA.Nutr_No, NUT_DATA.Nutr_Val, NUTR_DEF.NutrDesc\n" + 
						"FROM FOOD_DES, NUT_DATA, NUTR_DEF\n" + 
						"WHERE FOOD_DES.Long_Desc LIKE \"?\"\n" + 
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
		return result;
		
	}
	
	// End of nutrition database query code
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











