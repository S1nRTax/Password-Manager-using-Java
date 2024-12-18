package gui;

import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JFrame;



public class GUI extends JFrame{

        // constructor:
        public GUI(){
            this.setTitle("Password Manager.");
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setResizable(false);
            this.setSize(500, 500);
            this.setVisible(true);

            ImageIcon image = new ImageIcon("../passwordManager.png"); // create an Image icon
            this.setIconImage(image.getImage()); //set it to the frame

            this.getContentPane().setBackground(Color.DARK_GRAY);// set the background color to dark gray.
        }
}