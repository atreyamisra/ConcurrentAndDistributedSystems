//am73676_sr39533
import java.util.ArrayList;

public class OrderLog {
	
	int numOrders = 0;
	ArrayList<Order> list = new ArrayList<Order>();
	
	public synchronized void add(Order o){
		list.add(o);
	}
	
	//cancels an order and returns its contents
	public synchronized Order cancel(int id){
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).getId() == id){
				Order o = list.get(i);
				list.remove(i);
				return o;
			}
		}
		
		return null;
	}
	
	//Given a user, returns a list of all his orders
	//If the user isnt found, returns an empty list
	public synchronized ArrayList<Order> search(String user){
		ArrayList<Order> userList = new ArrayList<Order>();
		
		for(Order o: list){
			if(o.getUser().equals(user))
				userList.add(o);
		}
		
		return userList;
	}
	
	//given a list of orders, return a string containing all of their info
	public synchronized String ordersToString(ArrayList<Order> o){
		String s = "";
		for(Order i: o){
			s+= i.getId() + ", " + i.getProduct() + ", " + i.getQuantity() + "\n";
		}
		
		return s.trim(); //trim the last new line
	}
}
