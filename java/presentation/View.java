package presentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
/**
 * The View class acts as a parser. It takes the input from a text file and calls the corresponding function in the Controller class for each command given as input
 * @author Flueran Robert-Denis
 *
 */
public class View {
	Controller controller = new Controller();
	
	public void readFromFile(File file) {
		try {
			Scanner myReader = new Scanner(file);
			while(myReader.hasNextLine()) {
				String operations = myReader.nextLine();
				String[] arguments = operations.split(" ");
				if(arguments[0].compareToIgnoreCase("Insert") == 0) {
					controller.insert(arguments);
				}
				else
					if(arguments[0].compareToIgnoreCase("Delete") == 0) {
						controller.delete(arguments);
					}
					else
						if(arguments[0].compareToIgnoreCase("Report") == 0) {
							controller.report(arguments);
						}
						else
							if(arguments[0].compareToIgnoreCase("Order:") == 0) {
								controller.order(arguments);
							}
			}
			myReader.close();
		}catch (FileNotFoundException e) {
		      System.out.println("An error occurred. Couldn't find the file");
		      e.printStackTrace();
		}
	}
}
