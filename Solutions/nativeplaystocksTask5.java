/*
* PURPOSE: Store stock data directly to InterSystems IRIS Data Platform using a custom structure and call methods and
*  and routines in InterSystems IRIS.
* 
* NOTES: To use locally, make sure to change the IP and port of dbUrl to values for
*  your instance: jdbc:IRIS://YourIP:YourPort/USER
* When running, 
* 1. Notice the version of InterSystems IRIS automatically printed. This calls a routine within InterSystems IRIS. 
* 2. Choose option 2 to store stock data natively.
* 3. Choose option 3 to retrieve stock data natively.
* 4. Choose option 4 to generate trades with random data using methods from InterSystems IRIS.
*/

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.sql.Date;
import java.util.Scanner;
import java.math.BigDecimal;
import java.sql.ResultSet;

import com.intersystems.jdbc.IRISConnection;
import com.intersystems.jdbc.IRIS;
import com.intersystems.jdbc.IRISIterator;

import Demo.Trade;

import com.intersystems.jdbc.IRISDataSource;

public class nativeplaystocksTask5 {

	public static void main(String[] args) {
		String dbUrl = "jdbc:IRIS://127.0.0.1:51773/USER";
		String user = "SuperUser";
		String pass = "SYS";
		
		try {
			// Making connection
			IRISDataSource ds = new IRISDataSource();
			ds.setURL(dbUrl);
			ds.setUser(user);
			ds.setPassword(pass);
			IRISConnection dbconnection = (IRISConnection) ds.getConnection();
			System.out.println("Connected to InterSystems IRIS via JDBC.");

			// Create IRIS Native and call routines directly
			IRIS irisNative = IRIS.createIRIS(dbconnection);
			System.out.println("on InterSystems IRIS version: " + irisNative.functionString("PrintVersion","^StocksUtil"));
			
			boolean always = true;
			Scanner scanner = new Scanner(System.in);
			while (always) {
				System.out.println("1. Test");
				System.out.println("2. Store stock data");
				System.out.println("3. View stock data");
				System.out.println("4. Generate Trades");
				System.out.println("5. Quit");
				System.out.print("What would you like to do? ");

				String option = scanner.next();
				switch (option) {
				case "1":
					SetTestGlobal(irisNative);
					break;
				case "2":
					StoreStockData(irisNative, dbconnection);
					break;
				case "3":
					System.out.println("Printing nyse globals...");

					// Get current time
					Long startPrint = System.currentTimeMillis();

					PrintNodes(irisNative, "nyse");

					// Calculate execution time
					Long totalPrint = System.currentTimeMillis() - startPrint;
					System.out.println("Execution time: " + totalPrint + "ms");
					break;
				case "4":
					GenerateData(irisNative,10);
					break;
				case "5":
					System.out.println("Exited.");
					always = false;
					break;
				default: 
					System.out.println("Invalid option. Try again!");
					break;
				}				
			}
			scanner.close();					
			irisNative.close();
		}
		catch (SQLException e)
		{ 
			System.out.println("SQL error in application: " + e.getMessage());
		} 
		catch (Exception e) 
		{
			System.out.println("Error - Exception thrown: " + e.getMessage());
		} 
	}

    // Write to a test global
	public static void SetTestGlobal(IRIS irisNative)
	{
		irisNative.set(8888, "^testglobal", "1");
		Integer globalValue = irisNative.getInteger("^testglobal", "1");
		System.out.println("The value of ^testglobal(1) is " + globalValue);
	}

    // Store stock data directly into InterSystems IRIS
	public static void StoreStockData(IRIS irisNative, IRISConnection dbconnection)
	{
		// Clear global from previous runs
		irisNative.kill("^nyse");
		System.out.println("Storing stock data using Native API...");
		
		// Get stock data using JDBC and write global
		try {
			Statement myStatement = dbconnection.createStatement(); //needed for JDBC if doing SQL side-by-side
			ResultSet myRS = myStatement.executeQuery("select top 1000 transdate,name,stockclose,stockopen,high,low,volume from Demo.Stock");

			// Add stock data to list
			ArrayList<String> x = new ArrayList<>();
			while (myRS.next())
			{
				x.add(String.join(",", myRS.getString("name"), myRS.getString("transdate"), myRS.getString("high"), myRS.getString("low"), myRS.getString("stockopen"), myRS.getString("stockclose"), myRS.getString("volume")));			
			}
			int id=x.size();

			// Get current time
			Long startConsume = System.currentTimeMillis();

			// Loop through list of stock
			for (int i=0;i<id;i++)
			{
				irisNative.set(x.get(i),"^nyse",i+1);		
			}

	        // Get time consuming
			Long totalConsume = System.currentTimeMillis() - startConsume;
			System.out.println("Stored natively successfully. Execution time: " + totalConsume + "ms");

			myStatement.close();
		} 
		catch (SQLException e) {
			System.out.println("Error either retrieving data using JDBC or storing to globals:" + e.getMessage());
		} 
	}

	// Iterate over all nodes forwards and print
	public static void PrintNodes(IRIS irisNative, String globalName)
	{
		System.out.println("Iterating over " + globalName + " globals");
		
		// Create iter
		IRISIterator iter = irisNative.getIRISIterator(globalName);

		// Iterate over all nodes forwards
		System.out.println("walk forwards");
		while (iter.hasNext()) {
			String subscript = iter.next();
			System.out.println("subscript=" + subscript + ", value=" + iter.getValue());
		} 
	}
		
	// Helper method: to generate the list of trades
	public static Trade[] GenerateData(IRIS irisNative,Integer objectCount) {
		Trade[] data = new Trade[objectCount];
    	try{
    	   // Loop through list of trade to generate data for each trade
    	   for (int i=0;i<objectCount;i++) 
		   {
    		   Date tempDate = Date.valueOf("2018-01-01");
    		   Double tempAmount = irisNative.classMethodDouble("%PopulateUtils", "Currency");
    		   String tempName = irisNative.classMethodString("%PopulateUtils", "String") + irisNative.classMethodString("%PopulateUtils", "String") + irisNative.classMethodString("%PopulateUtils", "String");
    		   String tempTrader = irisNative.classMethodString("%PopulateUtils", "Name");
    		   int tempShares = new Random().nextInt(20)+1;
    		   data[i] = new Trade(tempName,tempDate,BigDecimal.valueOf(tempAmount),tempShares,tempTrader);
    		   System.out.println("New trade: " + tempName + " , " + tempDate + " , " + BigDecimal.valueOf(tempAmount) + " , " + tempShares + " , " + tempTrader);
    	   }
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}
		return data;
	}
}
