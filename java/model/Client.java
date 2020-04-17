package model;
/**
 * Java Class corresponding to "client" table in "warehouse" database. 
 * It has fields for each column of the table : id (primary key), name (varchar), city (varchar)
 * @author Flueran Robert-Denis
 *
 */
public class Client {
	private int id;
	private String name;
	private String city;
	
	public Client() {
		
	}
	public Client(int id, String name, String city){
		this.id = id;
		this.name = name;
		this.city = city;
	}
	
	public Client(String name, String city){
		this.name = name;
		this.city = city;
	}
	
	public Client(String name){
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	@Override
	public String toString() {
		return "Client [id=" + id + ", name=" + name + ", city=" + city	+ "]";
	}
}
