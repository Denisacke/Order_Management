package model;
/**
 * Java Class corresponding to "order" table in "warehouse" database. 
 * It has fields for each column of the table : id (primary key), client (varchar), sum (double)
 * @author Flueran Robert-Denis
 *
 */
public class Order {

	private int id;
	private String client;
	private double sum;
	
	public Order() {
		
	}

	public Order(int id, String client, double sum) {
		this.id = id;
		this.client = client;
		this.sum = sum;
	}
	
	
	public Order(String client, double sum) {
		this.client = client;
		this.sum = sum;
	}

	public Order(String client) {
		this.client = client;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public double getSum() {
		return sum;
	}
	public void setSum(double sum) {
		this.sum = sum;
	}
}
