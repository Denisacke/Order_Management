package model;
/**
 * Java Class corresponding to "orderitem" table in "warehouse" database. 
 * It has fields for each column of the table : client (varchar), product (varchar), quantity (int)
 * @author Flueran Robert-Denis
 *
 */
public class OrderItem {

	private String client;
	private String product;
	private int quantity;
	
	public OrderItem() {
		
	}
	public OrderItem(String client, String product, int quantity){
		this.client = client;
		this.product = product;
		this.quantity = quantity;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	
}
