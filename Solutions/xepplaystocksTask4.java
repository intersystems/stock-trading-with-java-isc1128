/*
* PURPOSE: Show XEP surpasses JDBC on inserting objects into database
*
* NOTES: To use locally, change the IP and port of dbUrl to values for your
*  instance: xepPersister.connect("YourIP",YourPort,"USER",user,pass);
* When running the application:
* 1. Choose option 3 to generate 10000 trades
* 2. Choose option 5 to generate 10000 trades
* 3. Compare time of two options
*/

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Scanner;

import com.intersystems.xep.*;

import Demo.Trade;

public class xepplaystocksTask4 {
	  public static void main(String[] args) throws Exception {
	    try {
			String user = "SuperUser";
			String pass = "SYS";
	    	
	    	// Initialize sampleArray to hold Trade items
	    	Trade[] sampleArray = null;
	    	
	    	// Connect to database using EventPersister
	        EventPersister xepPersister = PersisterFactory.createPersister();
	        xepPersister.connect("127.0.0.1",51773,"USER",user,pass);
			System.out.println("Connected to InterSystems IRIS.");
	        xepPersister.deleteExtent("Demo.Trade");   // remove old test data
	        xepPersister.importSchema("Demo.Trade");   // import flat schema
	       
	        // Create Event
	        Event xepEvent = xepPersister.getEvent("Demo.Trade");

	        
	        // Starting interactive prompt
			boolean always = true;
			Scanner scanner = new Scanner(System.in);
			while (always) {
				System.out.println("1. Make a trade (do not save)");
				System.out.println("2. Confirm all trades");
				System.out.println("3. Generate and save multiple trades");
				System.out.println("4. Retrieve all trades; show execution statistics");
				System.out.println("5. JDBC Comparison - Create and save multiple trades");
				System.out.println("6. Quit");
				System.out.print("What would you like to do? ");
				
				String option = scanner.next();
				switch (option) {
				case "1":
					//Create trade object
					System.out.print("Stock name: ");
					String name = scanner.next();
					
					System.out.print("Date (YYYY-MM-DD): ");
					Date tempDate = Date.valueOf(scanner.next());
										
					System.out.print("Price: ");
					BigDecimal price = scanner.nextBigDecimal();
					
					System.out.print("Number of Shares: ");
					int shares = scanner.nextInt();
					
					System.out.print("Trader name: ");
					String traderName = scanner.next();
					
					sampleArray = CreateTrade(name,tempDate,price,shares,traderName,sampleArray);
					break;
				case "2":
					//Save trades
					System.out.println("Saving trades.");
					XEPSaveTrades(sampleArray, xepEvent);
					sampleArray = null;
					break;
				case "3":
					System.out.print("How many items do you want to generate? ");	
					int number = scanner.nextInt();
					
					//Get sample generated array to store
					sampleArray = Trade.generateSampleData(number);
					
					//Save generated trades
					Long totalStore = XEPSaveTrades(sampleArray,xepEvent);
					System.out.println("Execution time: " + totalStore + "ms");
					break;
				case "4":
					System.out.println("TO DO: Retrieve all trades");
					break;
				case "5":
					System.out.print("How many items to generate using JDBC? ");
					int numberJDBC = scanner.nextInt();
					
					//Get sample generated array to store
					sampleArray = Trade.generateSampleData(numberJDBC);
					
					//Save generated trades using JDBC
					Long totalJDBCStore = StoreUsingJDBC(xepPersister,sampleArray);
					System.out.println("Execution time: " + totalJDBCStore + "ms");
					break;
				case "6":
					System.out.println("Exited.");
					always = false;
					break;
				default: 
					System.out.println("Invalid option. Try again!");
					break;
				}
				
			}
			scanner.close();	
	        xepEvent.close();
	        xepPersister.close();
		} catch (XEPException e) { 
			System.out.println("Interactive prompt failed:\n" + e); 
		}
	   } // end main()

	// Create sample and add it to the array
	public static Trade[] CreateTrade(String stockName, Date tDate, BigDecimal price, int shares, String trader, Trade[] sampleArray)
	{
		Trade sampleObject = new Trade(stockName,tDate,price,shares,trader); //
		System.out.println("New Trade: " + shares + " shares of " + stockName + " purchased on date " + tDate.toString() + " at price " + price + " by " + trader + ".");
		
		int currentSize = 0;
		int newSize = 1;
		if (sampleArray != null)
		{
			currentSize = sampleArray.length;
			newSize = currentSize + 1;
		} 
		
		Trade[] newArray = new Trade[ newSize ];
		for (int i=0; i < currentSize; i++)
		{
			newArray[i] = sampleArray[i];
		}
		newArray[newSize- 1] = sampleObject;
		System.out.println("Added " + stockName + " to the array. Contains " + newSize + " trade(s).");
		return newArray;
	}

	// Save array of trade into database using xepEvent
	public static Long XEPSaveTrades(Trade[] sampleArray,Event xepEvent)
	{
		Long startTime = System.currentTimeMillis(); //To calculate execution time
		xepEvent.store(sampleArray);
		Long totalTime = System.currentTimeMillis() - startTime;
		System.out.println("Saved " + sampleArray.length + " trade(s).");
		return totalTime;
	}

	// Save array of trade into database using JDBC - which is slower than using xepEvent
	public static Long StoreUsingJDBC(EventPersister persist, Trade[] sampleArray)
	{
		Long totalTime = new Long(0);
		
		//Loop through objects to insert
		try {
			String sql = "INSERT INTO Demo.Trade (purchaseDate,purchaseprice,stockName) VALUES (?,?,?)";
	
			PreparedStatement myStatement = persist.prepareStatement(sql);
			myStatement.setString(1, "2016-08-12");
			
			Long startTime = System.currentTimeMillis();
			for (int i=0; i < sampleArray.length; i++)
			{
				myStatement.setBigDecimal(2, sampleArray[i].purchasePrice);
				myStatement.setString(3, sampleArray[i].stockName);
				myStatement.addBatch();
			}
			myStatement.executeBatch();
			totalTime = System.currentTimeMillis() - startTime;	
			System.out.println("Inserted " + sampleArray.length + " item(s) via JDBC successfully.");
			myStatement.close();
		} catch (SQLException e) {
			System.out.println("There was a problem storing items using JDBC");
			e.getMessage();
		}
		return totalTime;
	}

} 