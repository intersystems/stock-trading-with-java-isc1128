/*
* PURPOSE: Make connection to InterSystems IRIS using XEP
*
* NOTES: To use locally, change the IP and port of dbUrl to values for your
*  instance: xepPersister.connect("YourIP",YourPort,"USER",user,pass);
*/

import com.intersystems.xep.*;

import Demo.Trade;

public class xepplaystocksTask1 {
	  public static void main(String[] args) throws Exception {
	    try {
			String user = "SuperUser";
			String pass = "SYS";
	    	
	    	// Connect to database using EventPersister
	        EventPersister xepPersister = PersisterFactory.createPersister();
	        xepPersister.connect("127.0.0.1",51773,"USER",user,pass);
			System.out.println("Connected to InterSystems IRIS.");
	        xepPersister.deleteExtent("Demo.Trade");   // remove old test data
	        xepPersister.importSchema("Demo.Trade");   // import flat schema
	       
	        // Create Event
	        Event xepEvent = xepPersister.getEvent("Demo.Trade");
	
	        xepEvent.close();
	        xepPersister.close();
		} catch (XEPException e) { 
			System.out.println("Interactive prompt failed:\n" + e); 
		}
	   } // end main()
	  
} 