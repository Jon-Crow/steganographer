package crow.jonathan.stegonographer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;

public class ImageSteganographer extends Steganographer
{
    private BufferedImage img;
    private int x, y;
    private boolean clearPx;
    
    public ImageSteganographer(BufferedImage img)
    {
        this.img = img;
        x = 0;
        y = 0;
        clearPx = false;
    }
    private void nextPixel()
    {
        x++;
        if(x >= img.getWidth())
        {
            x = 0;
            y++;
            if(y >= img.getHeight())
                y = 0;
        }
    }
    public boolean shouldClearPixels()
    {
        return clearPx;
    }
    public void setClearPixels(boolean clearPx)
    {
        this.clearPx = clearPx;
    }
    public BufferedImage getImage()
    {
        return img;
    }
    @Override
    public void injectByte(byte b)
    {
        int bit, rgb;
        for(int i = 0; i < Byte.SIZE; i++)
        {
            bit = b&(LSB_MASK << i);
            rgb = clearPx ? 0 : img.getRGB(x, y);
            
            if(bit != 0)
                rgb |= LSB_MASK;
            else
                rgb &= ~LSB_MASK;
            
            img.setRGB(x, y, rgb);
            nextPixel();
        }
    }
    @Override
    public byte ejectByte()
    {
        byte b = 0;
        int rgb, bit;
        for(int i = 0; i < Byte.SIZE; i++)
        {
            rgb = img.getRGB(x, y);
            bit = rgb&LSB_MASK;
            
            if(bit != 0)
                b |= (LSB_MASK << i);
            else
                b &= ~(LSB_MASK << i);
            
            nextPixel();
        }
        return b;
    }
    @Override
    public boolean fileWillFit(File f)
    {
        long space = f.length();
        if(space > Integer.MAX_VALUE)
            return false;
        space *= Byte.SIZE;
        space += Integer.BYTES*Byte.SIZE;
        space += f.getName().length()*Byte.SIZE;
        return space <= img.getWidth()*img.getHeight();
    }
    @Override
    public boolean save(File f)
    {
        try
        {
            ImageIO.write(img, "PNG", f);
            return true;
        }
        catch(Exception err)
        {
            err.printStackTrace();
            return false;
        }
    }
    
    public static ImageSteganographer fromFile(File f)
    {
        try
        {
            return new ImageSteganographer(ImageIO.read(f));
        }
        catch(Exception err)
        {
            err.printStackTrace();
            return null;
        }
    }
}
