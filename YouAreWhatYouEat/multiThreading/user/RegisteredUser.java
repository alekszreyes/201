package user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import DatabaseDriver.DatabaseDriver;
import diet.Diet;

public class RegisteredUser extends User{
	// Member diets exist for fast retrieval and structure formalization, though it is unnecessary
	HashMap<Integer, Diet> diets;
	Set<Integer> followedUser;
	Set<Integer> followingUser;
	int sessionCnt;
	public RegisteredUser(String name, String email) {
		// TODO Auto-generated constructor stub
		userCnt++;
		id = userCnt;
		username = name;
		this.email = email;
		
		// TODO: fetch all diets from the database for the particular user
		// This part is optional regarding to the structure of the other parts
		
		sessionCnt = 0;
	} 
	
	// Retrieve the existing user from the data base
	public RegisteredUser(int id, String email) {
		// TODO: get all information
		// Currently filled with test values
		userCnt++;
		this.id = id;
		this.username = "User"+id;
		this.email = email;
		followingUser = new HashSet<Integer>();
		DatabaseDriver dbd = new DatabaseDriver();
		dbd.connect();
		ArrayList<Map<String, String> > followers = dbd.getFollowers(id);
		for (Map<String, String> f: followers) {
			System.out.println("Following: " + f.get("userID"));
			this.followingUser.add(Integer.parseInt(f.get("userID")));
		}
		sessionCnt = 0;
	}
	
	public boolean isRegistered() {
		return true;
	}
	
	public Diet fetchDiet(int id) {
		return diets.get(id);
	}
	
	public void addDiet(int id, Diet d) {
		diets.put(id, d);
		// TODO: Add the diets back to the database. After this step is finished, the id is self-generated
	}
	
	public void removeDiet(int id) {
		diets.remove(id);
		// TODO: Remove the diet from the database
	}
	
	public void addSession() {
		sessionCnt++;
	}
	
	public void removeSession() {
		sessionCnt--;
	}
	
	public boolean isFollowing(int fid) {
		return followingUser.contains(fid);
	}
	
	public boolean noSession() {
		return (sessionCnt == 0);
	}
	
	public int getSessionCnt() {
		return sessionCnt;
		
	}
 
}
