package user;

public class Guest extends User {

	public Guest() {
		// TODO Auto-generated constructor stub
		userCnt++;
		id = userCnt;
		username = "Guest" + id;
		email = null;
	}
	
	@Override
	public boolean isRegistered() {
		return false;
	}

}
