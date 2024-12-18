package db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import util.PasswordUtil;

public class DatabaseManager {

    private static final String DB_DIRECTORY = "db";
    private static final String DB_FILE = "password_manager.db";
    private static final String DB_URL;

    static {
        File dbDir = new File(DB_DIRECTORY);
        if(!dbDir.exists()){
            dbDir.mkdir();
        }
        DB_URL = "jdbc:sqlite:" + DB_DIRECTORY + "/" + DB_FILE ;
    }
    
   
    public static String getDbUrl(){
        return DB_URL;
    }

    // Initialize the database.
    public static void initializeDB(){
        try(Connection conn = DriverManager.getConnection(DB_URL)){
            if(conn != null){
                String createEmailsTable = """
                        CREATE TABLE IF NOT EXISTS Emails (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            email TEXT UNIQUE NOT NULL
                        );
                        """;

                String createAccountsTable = """
                        CREATE TABLE IF NOT EXISTS Accounts (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        email_id INTEGER NOT NULL,
                        platform TEXT NOT NULL,
                        hashed_password TEXT NOT NULL,
                        FOREIGN KEY (email_id) REFERENCES Emails(id)
                        );
                        """;

                try(Statement stmt = conn.createStatement()){
                    stmt.execute(createEmailsTable);
                    stmt.execute(createAccountsTable);
                }
            }
        }catch(SQLException e){
            System.err.println("Error initializing the db: "+e.getMessage());
        }
    }  
    
    
    // Method to insert E-mails.
    public static void insertEmail(String email) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             var prepStmt = conn.prepareStatement("INSERT INTO Emails(email) VALUES(?)")) {
                
            System.out.println("Connection to SQLite has been established.");
            
            prepStmt.setString(1, email); 
            prepStmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }
    }
    
    // to commit.
    // Method to query E-mails from the database.
    // returns -1 if email not found.
    public static int queryEmails(String Email) {
    	
    	int id = -1; 
        String query = "SELECT id FROM Emails WHERE email = ?";
        
		try (Connection conn = DriverManager.getConnection(DB_URL);
				var prepStmt = conn.prepareStatement(query) ){
				
			prepStmt.setString(1,Email);
			ResultSet rs = prepStmt.executeQuery();
			
			if (rs.next()) { // If a record is found
	            id = rs.getInt("id"); // Retrieve the ID
	        } else {
	            System.out.println("Email not found in the database.");
	        }
			

		}catch(SQLException e) {
			System.err.println("Error connection to the database: "+ e.getMessage());
		}
		
		return id;
    }
    
    // to commit.
    // Method to insert Accounts with hashed password.
    public static void insertAccount(String platform , String password , String email) {
    	
    	String query = "INSERT INTO Accounts(email_id, platform, hashed_password) VALUES(?,?,?)";
    	
    	try (Connection conn = DriverManager.getConnection(DB_URL); 
			 var prepStmt = conn.prepareStatement(query)) {
    		
    		int id = queryEmails(email);
    		String hashedPassword = PasswordUtil.hashPassword(password);
    		
    		
			 prepStmt.setInt(1, id);
			 prepStmt.setString(2, platform);
			 prepStmt.setString(3, hashedPassword);
			 prepStmt.executeUpdate();
			 
    	}catch(SQLException e) {
    		System.err.println(e.getMessage());
    	}
    	
    }
    
    
    
    
    
    
    
    
    
    
}