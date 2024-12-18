package db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
    
    
    // Method to insert E-mails to the database.
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
    
    
    
    
    
    
    
    
    
    
}