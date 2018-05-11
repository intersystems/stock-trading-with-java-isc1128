import java.sql.SQLException;
import java.sql.Statement;

import com.intersystems.jdbc.IRIS;
import com.intersystems.xep.Event;
import com.intersystems.xep.EventPersister;
import com.intersystems.xep.PersisterFactory;
import com.intersystems.jdbc.IRISConnection;

public class multiplayTask1 {

	public static void main(String[] args) {
		String user = "SuperUser";
		String pass = "SYS";
		
		try {
			// Connect to database using EventPersister, which is based on IRISDataSource
	        EventPersister xepPersister = PersisterFactory.createPersister();
	        xepPersister.connect("127.0.0.1",51773,"User",user,pass); 
	        System.out.println("Connected to InterSystems IRIS via JDBC.");
	        xepPersister.deleteExtent("Demo.StockInfo");   // remove old test data
	        xepPersister.importSchema("Demo.StockInfo");   // import flat schema
	       
	        //***Initializations***
	        //Create XEP Event for object access
	        Event xepEvent = xepPersister.getEvent("Demo.StockInfo");

	        //Create JDBC statement object for SQL and IRIS Native access
	        Statement myStatement = xepPersister.createStatement();
	        
	        //Create IRIS Native object
	        IRIS irisNative = IRIS.createIRIS((IRISConnection)xepPersister);
	        
	        
	        //***Future code here***
	        
			
			//Close everything
		    xepEvent.close();
		    xepPersister.close();
						
		} catch (SQLException e) {
			 System.out.println("Error creating stock listing");
		}
	        
	}
}