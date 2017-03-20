//am73676_sr39533
import java.util.ArrayList;

public class Order {
	
	private String user;
	private String product;
	private int quantity;
	private int id;
	
	private static OrderLog log = new OrderLog();
	private static int currentId = 1;
	
	public Order(String user, String product, int quantity){
		synchronized(log){ //synchronize this block to avoid two orders getting the same ID
			id = currentId; //assign the id of the current order to the next consecutive ID
			currentId++; //increment the ID counter for the next order
		}
		this.user = user;
		this.product = product;
		this.quantity = quantity;
		log.add(this); //automatically add this new order to the log of orders
	}
	
	public int getId(){
		return id;
	}
	
	public String getUser(){
		return user;
	}
	
	public String getProduct(){
		return product;
	}
	
	public int getQuantity(){
		return quantity;
	}
	
	//cancels an order by removing it from the log
	//returns whether or not the cancel was successful
	public static boolean cancel(int id){
		Order o = log.cancel(id);
		
		return o != null;
	}
	
	//Given a user, returns a formatted String of all his orders
	//If the user has no orders, returns a failure message
	public static String search(String user){
		ArrayList<Order> o = log.search(user);
		
		if(o.size() > 0)
			return log.ordersToString(o);
		
		return "No order found for " + user;
	}
}
