package crow.jonathan.stegonographer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

public class MainPanel extends JPanel
{
    private JButton loadBtn, injectBtn, ejectBtn;
    private JLabel imgLbl;
    private ImageSteganographer steg;
    
    public MainPanel()
    {
        setLayout(new MigLayout());
        add(loadBtn = new JButton("Load Image"));
        add(injectBtn = new JButton("Inject File"));
        add(ejectBtn = new JButton("Eject File"), "wrap");
        add(new JScrollPane(imgLbl = new JLabel("Please load an image")), "span 3, grow, wrap");
        
        loadBtn.addActionListener(btnListen);
        injectBtn.addActionListener(btnListen);
        ejectBtn.addActionListener(btnListen);
    }
    private File selectFile()
    {
        JFileChooser jfc = new JFileChooser();
        jfc.showOpenDialog(this);
        File f = jfc.getSelectedFile();
        return f;
    }
    private final ActionListener btnListen = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent event) 
        {
           if(event.getSource() == loadBtn)
           {
               File f = selectFile();
               if(f != null)
               {
                   try
                   {
                       BufferedImage img = ImageIO.read(f);
                       steg = new ImageSteganographer(img);
                       imgLbl.setIcon(new ImageIcon(img));
                       imgLbl.setText("");
                       Main.getMainFrame().pack();
                   }
                   catch(Exception err)
                   {
                       imgLbl.setText("Could not load image");
                       err.printStackTrace();
                   }
               }
           }
           else if(event.getSource() == injectBtn)
           {
               File f = selectFile();
               if(steg != null && f != null)
               {
                   if(steg.injectFile(f))
                   {
                       File out = new File("secret.png");
                       steg.save(out);
                       JOptionPane.showMessageDialog(MainPanel.this, "Injected image saved to\n" + out.getAbsolutePath(), "Success", JOptionPane.PLAIN_MESSAGE);
                   }
                   else
                       JOptionPane.showMessageDialog(MainPanel.this, "Could not inject file.", "Error", JOptionPane.ERROR_MESSAGE);
               }
           }
           else if(event.getSource() == ejectBtn)
           {
               if(steg != null)
               {
                   if(steg.ejectFile())
                       JOptionPane.showMessageDialog(MainPanel.this, "Secret file ejected.", "Success", JOptionPane.PLAIN_MESSAGE);
                   else
                       JOptionPane.showMessageDialog(MainPanel.this, "Could not eject file.", "Error", JOptionPane.ERROR_MESSAGE); 
               }
           }
        }
    };
}
