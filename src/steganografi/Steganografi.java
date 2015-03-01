/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steganografi;

import javax.swing.JApplet;
import javax.swing.JFrame;

/**
 *
 * @author Fahziar
 */
public class Steganografi {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        JApplet mainControl = new MainWindow();
        mainControl.init();
        
        JFrame frame = new JFrame("Steganografi");
        frame.getContentPane().add(mainControl);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(370, 340);
        frame.show();
    }
}
