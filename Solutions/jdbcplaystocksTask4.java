/*
* PURPOSE: Add several portfolio items.
* 
* NOTES: To use locally, make sure to change the IP and port of dbUrl to values for
*  your instance: jdbc:IRIS://YourIP:YourPort/USER
* When running, 
* 1. Choose option 1 to see list of stocks.
* 2. Choose option 2 to create portfolio.
* 3. Choose option 3 and add stocks using names from the previous list of stocks.
*/
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Scanner;

import com.intersystems.jdbc.IRISDataSource;

public class jdbcplaystocksTask4 {
	
	public static void main(String[] args) {
		String dbUrl = "jdbc:IRIS://127.0.0.1:51773/USER";
		String user = "superuser";
		String pass = "SYS";
		
		try {
			// Making connection
			IRISDataSource ds = new IRISDataSource(); 
			ds.setURL(dbUrl);
			ds.setUser(user);
			ds.setPassword(pass);
			Connection dbconnection = ds.getConnection();
			System.out.println("Connected to InterSystems IRIS via JDBC.");
			
			//Starting interactive prompt
			boolean always = true;
			Scanner scanner = new Scanner(System.in);
			while (always) {
				System.out.println("1. View top 10");
				System.out.println("2. Create Portfolio table");
				System.out.println("3. Add to Portfolio");
				System.out.println("4. Update Portfolio");
				System.out.println("5. Delete from Portfolio");
				System.out.println("6. View Portfolio");
				System.out.println("7. Quit");
				System.out.print("What would you like to do? ");

				String option = scanner.next();
				switch (option) {
				case "1":
					System.out.print("On which date? (YYYY-MM-DD) ");
					String queryDate = scanner.next();
					FindTopOnDate(dbconnection, queryDate);
					break;
				case "2":
					System.out.println("Creating table...");
					CreatePortfolioTable(dbconnection);
					break;
				case "3":
					System.out.print("Name: ");
					String name = scanner.next();
					System.out.print("Date: ");
					String tDate = scanner.next();
					System.out.print("Price: ");
					String price = scanner.next();
					System.out.print("Number of shares: ");
					int shares = scanner.nextInt();
					AddPortfolioItem(dbconnection,name,tDate,price,shares);
					break;
				case "4":
					System.out.println("TO DO: Update Portfolio");
					break;
				case "5":
					System.out.println("TO DO: Delete from Portfolio");
					break;
				case "6":
					System.out.println("TO DO: View Portfolio");
					break;
				case "7":
					System.out.println("Exited.");
					always = false;
					break;
				default: 
					System.out.println("Invalid option. Try again!");
					break;
				}			
			}
			scanner.close();
			dbconnection.close();		
		}
		catch ( SQLException e) 
		{ 
			System.out.println(e.getMessage());
		} 
	}

    //Find top 10 stocks on a particular date
	public static void FindTopOnDate(Connection dbconnection, String onDate)
	{
		try 
		{
			String sql = "SELECT distinct top 10 transdate,name,stockclose,stockopen,high,low,volume FROM Demo.Stock WHERE transdate= ? ORDER BY stockclose desc";
			PreparedStatement myStatement = dbconnection.prepareStatement(sql);
			myStatement.setString(1, onDate);
			ResultSet myRS = myStatement.executeQuery();
			
			System.out.println("Date\t\tName\tOpening Price\tDaily High\tDaily Low\tClosing Price\tVolume");
			while (myRS.next()) 
			{
	            Date date = myRS.getDate("TransDate");
	            BigDecimal open = myRS.getBigDecimal("StockOpen");
	            BigDecimal high = myRS.getBigDecimal("High");
	            BigDecimal low = myRS.getBigDecimal("Low");
	            BigDecimal close = myRS.getBigDecimal("StockClose");
	            int volume = myRS.getInt("Volume");
	            String name = myRS.getString("Name");
	          
	            System.out.println(date + "\t" + name + "\t" + open + "\t" + high+ "\t" + low + "\t" + close+ "\t" + volume);
	        }
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}

	// Create portfolio table
	public static void CreatePortfolioTable(Connection dbconnection) 
	{
		String createTable = "CREATE TABLE Demo.Portfolio(Name varchar(50) unique, PurchaseDate date, PurchasePrice numeric(10,4), Shares int, DateTimeUpdated datetime)";
		try 
		{
			Statement myStatement = dbconnection.createStatement();
			myStatement.executeUpdate(createTable);
			System.out.println("Created Demo.Portfolio table successfully.");
		} 
		catch (SQLException e) 
		{
			System.out.println("Table not created and likely already exists.");
			e.getMessage();
		}
	}

	// Add item to portfolio
	public static void AddPortfolioItem (Connection dbconnection, String name, String purchaseDate, String price, int shares)
	{
		// Get current time
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		try 
		{
			String sql = "INSERT INTO Demo.Portfolio (name,PurchaseDate,PurchasePrice,Shares,DateTimeUpdated) VALUES (?,?,?,?,?)";
			PreparedStatement myStatement = dbconnection.prepareStatement(sql);
			myStatement.setString(1, name);
			myStatement.setString(2, purchaseDate);
			myStatement.setString(3, price);
			myStatement.setInt(4, shares);
			myStatement.setString(5, timestamp.toString());
			myStatement.execute();
			System.out.println("Added new line item for stock " + name + ".");
		} 
		catch (SQLException e) 
		{
			// Error code 119 is uniqueness constraint violation
			if (e.getErrorCode() == 119) 
			{
				System.out.println(name + " is already in the portfolio.");
			}
			else 
			{
				System.out.println("Error adding portfolio item: " + e.getMessage());	
			}
		}
	}
	
}
	
