package user;

public abstract class User {
	protected String username;
	protected String email;
	protected int id;
	protected static int userCnt = 0;
	
//	User(String n, String em) {
//		username = n;
//		email = em;
//		userCnt++; 
//		id = userCnt; // After Database servlet is completed, will be replaced by the number returned by database
//	}
	
	public String getUsername() {
		return username;
	}
	
	public String getEmail() {
		return email;
	}
	
	public int getID() {
		return id;
	}
	
	public static int getUserCnt() {
		return userCnt;
	}
	
	public abstract boolean isRegistered();
	
	
}
