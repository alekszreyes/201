package user;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserList {
	private ConcurrentHashMap<Integer, User> userList;

	public UserList() {
		userList = new ConcurrentHashMap<>();
	}
	
	public void addUser(User u) {
		//TODO: integrate with database to retrieve the actual user ID
		int id = u.id;
		userList.put(id, u);
	}
	
	public ConcurrentHashMap<Integer, User> getList() {
		return userList;
	}
	
	public boolean isActive(int id) {
		return userList.containsKey(id);
	}
	
	/* public void upgrade(int id) {
		// TODO: implement if the guest class is required
	}*/
	
	public User getUser(int id) {
		return userList.get(id);
	}
	
	public void removeUser(int id) {
		userList.remove(id);
	}
	
	// Debug Helpers
	public void printUserMap() {
		for (Map.Entry<Integer, User> su: userList.entrySet()) {
			System.out.println("id=" + su.getKey() + " Name=" + su.getValue().getEmail() + " SessionCnt=" + ((RegisteredUser) su.getValue()).getSessionCnt());
		}
	}
	
	
}
