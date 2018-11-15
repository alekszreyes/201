package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class SummaryEngine
 */
@WebServlet("/SummaryEngine")
public class SummaryEngine extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Map<Integer, String> dayOfWeekConv;
	static {
		HashMap<Integer, String> temp = new HashMap<>();
		temp.put(Calendar.MONDAY, "Monday");
		temp.put(Calendar.TUESDAY, "Tuesday");
		temp.put(Calendar.WEDNESDAY, "Wednesday");
		temp.put(Calendar.THURSDAY, "Thursday");
		temp.put(Calendar.FRIDAY, "Friday");
		temp.put(Calendar.SATURDAY, "Saturday");
		temp.put(Calendar.SUNDAY, "Sunday");
		dayOfWeekConv = Collections.unmodifiableMap(temp);
	}
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SummaryEngine() {
        super();
        // TODO Auto-generated constructor stub
        
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String email = (String) request.getSession().getAttribute("userEmail");
    	System.out.println(email);
    	System.out.println("in summary engine");
    	String json = "";
    	String responseText = "";
    	ArrayList<String> axis = new ArrayList<>();
		ArrayList<Double> week1 = new ArrayList<>();
		ArrayList<Double> week2 = new ArrayList<>();
		if (email == null) {
    		responseText = "Error: email not revognized";
    	}
    	try {
    		Class.forName("com.mysql.jdbc.Driver");
    		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutrition?user=root&password=lunjohnzhang&useSSL=false");
    		String q = 
    				"SELECT Users.userID, DietUser.dietID, DietUser.creationTime, DietFood.foodID, NUT_DATA.NDB_No, NUT_DATA.Nutr_No, NUT_DATA.Nutr_Val "
    				+ "FROM Users, DietFood, DietUser, NUT_DATA "
    				+ "WHERE NUT_DATA.Nutr_No = 268 "
    				+ "AND Users.userEmail = ? "
    				+ "AND Users.userID = DietUser.userID "
    				+ "AND DietUser.dietID = DietFood.dietID "
    				+ "AND DietFood.foodID = NUT_DATA.NDB_No ";
    		
    		PreparedStatement ps = conn.prepareStatement(q);
    		
    		ps.setString(1, email);
    		ResultSet rs = ps.executeQuery();
    		HashMap<Integer, Double> result = new HashMap<>();
    		class DayCal {
				int day;
				double cals;
				DayCal(int d, double c) {
					day = d;
					c = cals;
				}
			}
    		int dayToday = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
    		while (rs.next()) {
    			//DEBUG
    			System.out.println("Got!");
    			System.out.println(rs.getInt("foodID") + " " + rs.getString("creationTime"));
    			Date d = rs.getDate("creationTime");
    			Calendar.Builder cb =  new Calendar.Builder();
    			cb.setInstant(d);
    			Calendar c = cb.build();
    			
    			int dayCreated = c.get(Calendar.DAY_OF_YEAR);
    			System.out.println(dayCreated + " " + dayToday);
    			int dayPast = dayToday - dayCreated;
    			System.out.println("datPast: " + dayPast);
    			if (dayPast <= 14 && dayPast >= 1) {
    				if (!result.containsKey(dayPast)) {
    					System.out.println("ADD " + dayPast);
    					result.put(dayPast, rs.getDouble("Nutr_Val"));
    				} else {
    					result.replace(dayPast, result.get(dayPast) + rs.getDouble("Nutr_Val"));
    					System.out.println("CHANGE " + dayPast);
    				}
    			}	
    		}
    		
    		// DEBUG
    		for (int i = 1; i <= 14; i++) {
    			if (!result.containsKey(i)) {
    				result.put(i, 0.0);
    			}
    		}
    		
    		ArrayList<DayCal> daycals = new ArrayList<>();
			for (int i = 1; i <= 14; i++) {
				daycals.add(new DayCal(i, result.get(i)));
			}
			
			int DayOfWeekToday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
			
			for (int i = 0; i < 7; i++) {
				axis.add(dayOfWeekConv.get(DayOfWeekToday + i));
				week1.add(result.get(14 - i));
				week2.add(result.get(7 - i));
			}
			
			if (responseText.isEmpty()) {
				responseText = "Success";
			}
			
			
    	} catch (SQLException sqle) {
    		System.out.println("sqle: " + sqle.getMessage());
    		responseText += "sqle: " + sqle.getMessage();
    	} catch (ClassNotFoundException cnfe) {
    		System.out.println("cnfe: " + cnfe.getMessage());
    		responseText += "cnfe: " + cnfe.getMessage();
    	}
    	
    	CalResponse r = new CalResponse(axis, week1, week2);
    	System.out.println(r.week2.get(6));
    	Gson gson = new Gson();
		String j = gson.toJson(r);
		// DEBUG
		System.out.println(j);
    	
		PrintWriter pw = response.getWriter();
		pw.print(j);
    	
    }

}

class CalResponse {
	ArrayList<String> axis;
	ArrayList<Double> week1;
	ArrayList<Double> week2;
	CalResponse(ArrayList<String> a, ArrayList<Double> w1, ArrayList<Double> w2) {
		axis = a;
		week1 = w1;
		week2 = w2;
	}
}
