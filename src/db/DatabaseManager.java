package db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import util.PasswordUtil;

public class DatabaseManager {

    private static final String DB_DIRECTORY = "db";
    private static final String DB_FILE = "password_manager.db";
    private static final String DB_URL;
    
    
    // Email pattern (regex) : 
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
    		"^[A-Za-z0-9+_.-]+@(.+)$"
    		);

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
    
    
    /**
     * Validate email address format.
     * @return true if email is valid, sinn false.
     * */
    private static boolean isValidEmail(String email) {
    	if(email == null || email.trim().isEmpty() ) {
    		return false;
    	}
    	return EMAIL_PATTERN.matcher(email).matches();
    }
    
    
    /**
     * Check si un email exists in the database.
     * @return true/false.
     * @throws SQLException
     */
    private static boolean emailExists(Connection conn, String email)
    	throws SQLException {
    	String sql = "SELECT COUNT(*) FROM Emails WHERE email= ?";
    	try (var stmt = conn.prepareStatement(sql)){
    		stmt.setString(1, email);
    		var resultat = stmt.executeQuery();
    		return resultat.next() && resultat.getInt(1) > 0;
     	}
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
    
    
    /**
     * Inserts a new email into the database with validation.
     * @param email
     * @return InsertResult enum.
     */
    public static InsertResult insertEmail(String email) {
    			
    			// validate the email.
    			if( !isValidEmail(email)) {
    				return InsertResult.INVALID_FORMAT;
    			}
    	
    	
        try (Connection conn = DriverManager.getConnection(DB_URL)){
        		// check if email already exists.
        		if(emailExists(conn,email)) {
        			return InsertResult.DUPLICATE_EMAIL;
        		}
        		
	             String sql = "INSERT INTO Emails(email) VALUES(?)";
	             try (var stmt =conn.prepareStatement(sql)) {
	            	 stmt.setString(1,email.trim().toLowerCase());
	            	 int rowsAffected = stmt.executeUpdate();
	            	 
	            	 return rowsAffected > 0 ? InsertResult.SUCCESS : InsertResult.FAILED;
	             }
        } catch (SQLException e) {
            System.err.println("Database error while inserting email: " + e.getMessage());
            return InsertResult.DATABASE_ERROR;
        }
    }
    
    
    // enum to store Error types.
    public enum InsertResult {
    	SUCCESS("Email successfully inserted"),
    	INVALID_FORMAT("Invalid email format"),
    	DUPLICATE_EMAIL("Email already exists"),
    	DATABASE_ERROR("Database error occurred"),
    	FAILED("Insert operation failed"),
    	INVALID_PLATFORM("Invalid platform name"),
    	INVALID_PASSWORD("Invalid password"),
    	EMAIL_NOT_FOUND("Email does not exist");
    	
    	
    	private final String message;
    	
    	InsertResult(String message){
    		this.message = message;
    	}
    	
    	public String getMessage() {
    		return message;
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
			
			if (rs.next()) { 
	            id = rs.getInt("id"); 
	        } else {
	            System.out.println("Email not found in the database.");
	        }
			

		}catch(SQLException e) {
			System.err.println("Error connection to the database: "+ e.getMessage());
		}
		
		return id;
    }
    
    
    /**
     * Method to insert Accounts with hashed password.
     * @param platform
     * @param password
     * @param email
     * @return errors.
     */
    public static InsertResult insertAccount(String platform , String password , String email) {
    	
    			// validation des inputs.
    			if(platform == null || platform.trim().isEmpty()) {
    				return InsertResult.INVALID_PLATFORM;
    			}
    			
    			if(password == null || password.trim().isEmpty()) {
    				return InsertResult.INVALID_PASSWORD;
    			}
    	
    	
    	
    	try (Connection conn = DriverManager.getConnection(DB_URL)){
			 // check if the email already exists in DB.
    			int emailId = queryEmails(email);
    			if(emailId == -1) {
    				return InsertResult.EMAIL_NOT_FOUND;
    			}
    			
    			// hash the password.
    			String hashedPassword = PasswordUtil.hashPassword(password);
    			
    			// insert the account.
    			String query = "INSERT INTO Accounts(email_id, platform, hashed_password) VALUES(?,?,?)";
    			try (var stmt = conn.prepareStatement(query)){
    				stmt.setInt(1, emailId);
    	            stmt.setString(2, platform.trim());
    	            stmt.setString(3, hashedPassword);
    	            
    	            int rowsAffected = stmt.executeUpdate();
    	            
    	            return rowsAffected > 0 ? InsertResult.SUCCESS : InsertResult.FAILED;
    			}
    	}catch(SQLException e) {
    		System.err.println("Database error while inserting account: " + e.getMessage());
	        return InsertResult.DATABASE_ERROR;
    	}
    	
    }
    
    
    
    
    
    
    
    
    
    
}