package bll;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
//import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;

import dao.OrderDAO;
import dao.OrderItemDAO;
import dao.ProductDAO;
import model.Order;
import model.OrderItem;
/**
 * The sole purpose of the class is to generate PDF with bills for each customer that has ordered something
 * The bill is going to include all items ordered by a customer along with their price and total costs
 * @author Flueran Robert-Denis
 * 
 */
public class OrderBLL {
	private ArrayList<Order> list;
	private OrderDAO order;
	private int billCount = 0;
	public OrderBLL() {
		order = new OrderDAO();
	}
	
	public void createPDF() {
		list = (ArrayList<Order>) order.findAll();
		
		try {
			for(int i = 0; i < list.size(); i++) {
				billCount++;
				Document document = new Document();
				PdfWriter.getInstance(document, new FileOutputStream("bill_" + billCount + ".pdf"));
				document.open();
				OrderItemDAO item = new OrderItemDAO();
				ArrayList<OrderItem> itemList = (ArrayList<OrderItem>) item.findAllByName(list.get(i).getClient());
				Font font = FontFactory.getFont(FontFactory.COURIER_BOLD, 20, BaseColor.BLACK);
				Font font2 = FontFactory.getFont(FontFactory.COURIER_BOLD, 12, BaseColor.BLACK);
				document.add(new Paragraph("Bill", font));
				document.add(Chunk.NEWLINE);
				document.add(new Paragraph("Client: " + list.get(i).getClient(), font));
				document.add(Chunk.NEWLINE);
				document.add(new Paragraph("Item list", font2));
				document.add(Chunk.NEWLINE);
				List list2 = new List();
				for(int j = 0; j < itemList.size(); j++) {
					ProductDAO product = new ProductDAO();
					list2.add(itemList.get(j).getProduct() + "         " + "price: " + product.findByName(itemList.get(j).getProduct()).getPrice() + " RON x" + 
					itemList.get(j).getQuantity());  
				}
			    document.add(list2); 
				
				document.add(Chunk.NEWLINE);
				document.add(new Paragraph("Total costs: " + list.get(i).getSum() + " RON", font));
				document.close();
			}
		} catch (Exception e)
	    {
	        e.printStackTrace();
	    }
		 
		
		return;
	}
}
