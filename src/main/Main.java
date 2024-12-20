package main;


import db.DatabaseManager;
import gui.GUI;

public class Main {

    public static void main(String[] args) {
        // initialize db.
        DatabaseManager.initializeDB();
        
        
        
        
        // start the GUI.
        GUI frame = new GUI();
    }
    
}
