package start;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import bll.OrderBLL;
import dao.ClientDAO;
import dao.ProductDAO;
import presentation.View;

/**
 * Main Class
 * @author Flueran Robert-Denis
 */
public class Start {
	protected static final Logger LOGGER = Logger.getLogger(Start.class.getName());

	public static void main(String[] args) throws SQLException {
		View view = new View();
		OrderBLL order = new OrderBLL();
		try {
			File file = new File(args[0]);
			view.readFromFile(file);
			order.createPDF();
			
		} catch (Exception ex) {
			LOGGER.log(Level.INFO, ex.getMessage());
		}

	}

}
