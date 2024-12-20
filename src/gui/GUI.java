package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import db.DatabaseManager;
import db.DatabaseManager.InsertResult;

public class GUI extends JFrame {

    // Constructor
    public GUI() {
        this.setTitle("Password Manager");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(500, 500);
        this.setLayout(null);

        // window icon
        ImageIcon image = new ImageIcon("src/gui/passwordManager.png");
        this.setIconImage(image.getImage());
        this.getContentPane().setBackground(Color.WHITE);

        // components
        JTextField emailField = new JTextField();
        emailField.setBounds(100, 10, 200, 30);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(100, 50, 100, 30);

        JLabel displayLabel = new JLabel("Enter your email");
        displayLabel.setBounds(100, 90, 300, 30);

        JLabel statusLabel = new JLabel("");
        statusLabel.setBounds(100, 130, 300, 30);
        statusLabel.setForeground(Color.RED);

        // Add ActionListener to the submit button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                
                // Insert email and handle the result
                InsertResult result = DatabaseManager.insertEmail(email);
                
                if (result == InsertResult.SUCCESS) {
                    statusLabel.setText("Email saved successfully!");
                    statusLabel.setForeground(Color.GREEN);
                    emailField.setText("");
                } else {
                    statusLabel.setText("Error: " + result.getMessage());
                    statusLabel.setForeground(Color.RED);
                }
            }
        });

        // Add components to the frame
        this.add(emailField);
        this.add(submitButton);
        this.add(displayLabel);
        this.add(statusLabel);

        // Make frame visible
        this.setVisible(true);
    }

    public static void main(String[] args) {
        // Run the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new GUI());
    }
}