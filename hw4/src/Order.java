//am73676_sr39533

public class Order {
	
	private String user;
	private String product;
	private int quantity;
	private int id;
	
	private static OrderLog log = new OrderLog(); //used to lock ID assignment
	private static int currentId = 1;
	
	public Order(String user, String product, int quantity){
		synchronized(log){ //synchronize this block to avoid two orders getting the same ID
			id = currentId; //assign the id of the current order to the next consecutive ID
			currentId++; //increment the ID counter for the next order
		}
		this.user = user;
		this.product = product;
		this.quantity = quantity;
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
}
