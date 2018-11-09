package diet;

import java.util.ArrayList;

public class Diet {
	ArrayList<FoodItem> items;
	
	public Diet() {
		// TODO Auto-generated constructor stub
		items = new ArrayList<FoodItem>();
	}
	
	public void addItem(FoodItem e) {
		items.add(e);
	}
	
	public ArrayList<FoodItem> getList() {
		return items;
	}
	
	public FoodItem getItem(int i) {
		return items.get(i);
	}

}
