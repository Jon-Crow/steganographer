package crow.jonathan.stegonographer;

import java.io.File;
import java.util.Scanner;
import javax.swing.JFrame;

public class Main 
{   
    private static JFrame mainFrame;
    
    public static void main(String[] args) 
    {
        mainFrame = new JFrame("Steganographer");
        mainFrame.setContentPane(new MainPanel());
        mainFrame.pack();
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setVisible(true);
    }
    public static JFrame getMainFrame()
    {
        return mainFrame;
    }
}
