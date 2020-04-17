package presentation;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.*;

import dao.*;
import model.Client;
import model.Order;
import model.OrderItem;
import model.Product;

/**
 * It contains all the functions required for parsing the input commands and calling the appropriate functions to perform operations on the database
 * @author Flueran Robert-Denis
 *
 */
public class Controller {

	private int productCount = 0;
	private int clientCount = 0;
	private int orderCount = 0;
	private int orderError = 0;
	private int orderedItem = 0;
	/**
	 * Method parses a given input line and inserts an OrderItem object into the database. It also updates the stock of the ordered product
	 * If the order can't be made due to lack of products, then a pdf file with an appropriate message is generated
	 * @param args
	 */
	public void order(String[] args) {
		
		OrderItemDAO obj = new OrderItemDAO();
		String client = new String();
		client = client.concat(args[1] + " ");
		args[2] = args[2].substring(0, args[2].length() - 1);
		client = client.concat(args[2]);
		args[3] = args[3].substring(0, args[3].length() - 1);
		String product = args[3];
		int quantity = Integer.parseInt(args[4]);
		
		OrderItem orderItem = new OrderItem(client, product, quantity);
		ProductDAO updateProduct = new ProductDAO();
		Product productToChange = updateProduct.findByName(product);
		productToChange.setStock(productToChange.getStock() - quantity);
		if(productToChange.getStock() < 0) {
			System.err.println("CANNOT ADD ORDER..NOT ENOUGH STOCK");
			orderError++;
			Document document = new Document();
			try {
				PdfWriter.getInstance(document, new FileOutputStream("order_error" + orderError + ".pdf"));
				document.open();
				Font font = FontFactory.getFont(FontFactory.COURIER, 9, BaseColor.BLACK);
				Chunk chunk = new Chunk("Cannot add order of " + quantity + " " + productToChange.getName() + "s " + "for " + client + " as there are not enough of them in stock", font);
				 
				document.add(chunk);
			} catch (Exception e)
		    {
		        e.printStackTrace();
		    }
			 
			document.close();
			return;
		}
		updateProduct.update(productToChange);
		obj.insert(orderItem);
		Document document = new Document();
		orderedItem++;
		try {
			PdfWriter.getInstance(document, new FileOutputStream("order_item" + orderedItem + ".pdf"));
			document.open();
			Font font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
			Chunk chunk = new Chunk(orderItem.getClient() + " ordered " + orderItem.getProduct() + " x" + orderItem.getQuantity(), font);
			 
			document.add(chunk);
		} catch (Exception e)
	    {
	        e.printStackTrace();
	    }
		document.close();
		OrderDAO order = new OrderDAO();
		Order bill = null;
		System.out.println("GOT TO BILL CREATION");
		if(order.findByName(client) == null) {
			bill = new Order(client, productToChange.getPrice() * quantity);
			System.out.println("CREATING BILL.......");
			order.insert(bill);
		}
		else {
			System.out.println("UPDATING BILL");
			bill = order.findByName(client);
			bill.setSum(bill.getSum() + productToChange.getPrice() * quantity);
			
			order.update(bill);
		}
	}
	
	/**
	 * Method parses a given input line and inserts a Client or a Product object into the database. 
	 * In case the product already exists in the database, the method updates its stock
	 * @param args
	 */
	public void insert(String[] args) {
		AbstractDAO obj;
		String name = new String();
		args[1] = args[1].substring(0, args[1].length() - 1);
		if(args[1].compareToIgnoreCase("client") == 0) {
			obj = new ClientDAO();
			name = name.concat(args[2] + " ");
			args[3] = args[3].substring(0, args[3].length() - 1);
			name = name.concat(args[3]);
			
			Client client = new Client(name, args[4]);
			obj.insert(client);
		}
		else
			if(args[1].compareToIgnoreCase("product") == 0) {
				obj = new ProductDAO();
				args[2] = args[2].substring(0, args[2].length() - 1);
				name = name.concat(args[2]);
				args[3] = args[3].substring(0, args[3].length() - 1);
				int quantity = Integer.parseInt(args[3]);
				double price = Double.parseDouble(args[4]);
				Product product = new Product(name, quantity, price);
				ProductDAO updateProduct = new ProductDAO();
				Product productToChange = null;
				
				if(updateProduct.findByName(name) == null) {
					obj.insert(product);
					
				}
				else {
					productToChange = updateProduct.findByName(name);
					productToChange.setStock(productToChange.getStock() + product.getStock());
					obj.update(productToChange);
				}
			}
	}
	
	/**
	 * Method parses a given input line and deletes a Client, a Product or an OrderItem object from the database. 
	 * @param args
	 */
	public void delete(String[] args) {
		AbstractDAO obj;
		String name = new String();
		args[1] = args[1].substring(0, args[1].length() - 1);
		if(args[1].compareToIgnoreCase("client") == 0) {
			obj = new ClientDAO();
			name = name.concat(args[2] + " ");
			args[3] = args[3].substring(0, args[3].length() - 1);
			name = name.concat(args[3]);
			Client client;
			if(args.length > 4) {
				client = new Client(name);
			}
			else {
				client = new Client(name, args[4]);
			}
			obj.delete(client);
			System.out.println("Delete: " + client.getName() + " " + client.getCity());
		}
		else
			if(args[1].compareToIgnoreCase("product") == 0) {
				obj = new ProductDAO();
				name = name.concat(args[2]);
				Product product = new Product(name);
				System.out.println("Delete: " + product.getName());
				obj.delete(product);
			}
			else
				if(args[1].compareToIgnoreCase("order") == 0) {
					obj = new OrderItemDAO();
					name = name.concat(args[2]);
					args[3] = args[3].substring(0, args[3].length() - 1);
					name = name.concat(args[3]);
					args[4] = args[4].substring(0, args[4].length() - 1);
					String product = args[4];
					int quantity = Integer.parseInt(args[5]);
					OrderItem order = new OrderItem(name, product, quantity);
					obj.delete(order);
				}
				else
					if(args[1].compareToIgnoreCase("bill") == 0) {
						obj = new OrderDAO();
						name = name.concat(args[2] + " ");
						name = name.concat(args[3]);
						Order order = new Order(name);
						obj.delete(order);
					}
					
	}
	
	/**
	 * Method parses a given input line and generates a pdf report with all records for one of the database's tables. 
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public void report(String[] args) {
		 Document document = new Document();
		 AbstractDAO obj = null;
		 ArrayList<?> list = null;
		 String pdfName = null;
		 if(args[1].compareToIgnoreCase("client") == 0) {
			 obj = new ClientDAO();
			 clientCount++;
			 pdfName = "client";
		 }
		 else
			 if(args[1].compareToIgnoreCase("product") == 0) {
				 obj = new ProductDAO();
				 productCount++;
				 pdfName = "product";
			 }
			 else
				 if(args[1].compareToIgnoreCase("order") == 0) {
					 obj = new OrderItemDAO();
					 orderCount++;
					 pdfName = "order";
				 }
		    try
		    {
		    	PdfWriter writer = null;
		    	if(pdfName.compareToIgnoreCase("client") == 0) {
		    		writer = PdfWriter.getInstance(document, new FileOutputStream("client" + "_" + clientCount + ".pdf"));
		    		list = (ArrayList<Client>) obj.findAll();
		    	}
		    	else
		    		if(pdfName.compareToIgnoreCase("product") == 0) {
		    			writer = PdfWriter.getInstance(document, new FileOutputStream("product" + "_" + productCount + ".pdf"));
		    			list = (ArrayList<Product>) obj.findAll();
		    		}
		    			
		    		else
		    			if(pdfName.compareToIgnoreCase("order") == 0) {
		    				writer = PdfWriter.getInstance(document, new FileOutputStream("order" + "_" + orderCount + ".pdf"));
		    				list = (ArrayList<OrderItem>) obj.findAll();
		    			}
			    			
		        document.open();
		        LinkedList<String> tableFields = obj.getTableFields(list.get(0));
		        PdfPTable table = new PdfPTable(tableFields.size());
		        table.setWidthPercentage(100);
		        table.setSpacingBefore(10f); 
		        table.setSpacingAfter(10f);
		        
		        for(int j = 0; j < tableFields.size(); j++) {
	        		PdfPCell cell1 = new PdfPCell(new Paragraph(tableFields.get(j)));
			        cell1.setPaddingLeft(10);
			        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			        table.addCell(cell1);
	        	}
		        float[] columnWidth = new float[tableFields.size()];
		        for(int i = 0; i < tableFields.size(); i++) {
		        	columnWidth[i] = 1f;
		        }
		        table.setWidths(columnWidth);
		        for(int i = 0; i < list.size(); i++) {
		        	LinkedList<?> fieldList = obj.retrieveProperties(list.get(i));
		        	for(int j = 0; j < fieldList.size(); j++) {
		        		PdfPCell cell4 = new PdfPCell(new Paragraph(fieldList.get(j).toString()));
		        		cell4.setPaddingLeft(10);
		        		table.addCell(cell4);
		        	}
		        }
		        document.add(table);
		        document.close();
		        writer.close();
		    } catch (Exception e)
		    {
		        e.printStackTrace();
		    }
	}
}
